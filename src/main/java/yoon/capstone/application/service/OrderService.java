package yoon.capstone.application.service;

import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.dto.request.KakaoReadyRequest;
import yoon.capstone.application.dto.request.OrderDto;
import yoon.capstone.application.dto.response.*;
import yoon.capstone.application.entity.*;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.OrderException;
import yoon.capstone.application.exception.UnauthorizedException;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.OrderRepository;
import yoon.capstone.application.repository.PaymentRepository;
import yoon.capstone.application.repository.ProjectsRepository;
import yoon.capstone.application.security.JwtAuthentication;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${KAKAOPAY_KEY}")
    private String admin_key;
    @Value("${SERVICE_URL}")
    private String serviceUrl;

    private final MemberRepository memberRepository;

    private final AesBytesEncryptor aesBytesEncryptor;

    private final OrderRepository orderRepository;

    private final ProjectsRepository projectsRepository;

    private final RedissonClient redissonClient;

    private OrderResponse toResponse(Orders orders){
        return new OrderResponse(orders.getMembers().getUsername(), orders.getMembers().getProfile(),
                orders.getPayment().getCost(), orders.getComments().getContent(), orders.getPayment().getCreatedAt());
    }

    private ProjectCache toCache(Projects projects){
        return new ProjectCache(projects.getProjectIdx(), projects.getTitle(), projects.getCurrentAmount(),
                projects.getGoalAmount(), projects.getParticipantsCount());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderList(long idx){
        //Eagle Loading
        List<Orders> list = orderRepository.findAllByProjectsIndexWithFetchJoin(idx);
        List<OrderResponse> result = new ArrayList<>();
        for(Orders o:list){
            result.add(toResponse(o));
        }
        return result;
    }

    @Transactional
    public KakaoPayResponse kakaoPayment(OrderDto dto){

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        String paymentCode = UUID.randomUUID().toString();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken)
            throw new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS); //로그인 되지 않았거나 만료됨

        JwtAuthentication memberDto = (JwtAuthentication) authentication.getPrincipal();

        //Read Cache Or Cache Warm
        RBucket<ProjectCache> rBucket = redissonClient.getBucket("projects::" + dto.getProjectIdx());
        if (rBucket.get() == null) {
            Projects projects = projectsRepository.findProjectsByProjectIdx(dto.getProjectIdx());
            rBucket.set(toCache(projects)); //Persist
        }
        //

        ProjectCache projects = rBucket.get();

        if(projects.getCurrentAmount() + dto.getTotal() > projects.getGoalAmount())
            throw new OrderException("목표금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);



        headers.set("Content-type", "application/json");
        headers.set("Authorization", "DEV_SECRET_KEY " + admin_key);

        KakaoReadyRequest kakaoRequest = new KakaoReadyRequest("TC0ONETIME",
                paymentCode, String.valueOf(memberDto.getMemberIdx()), projects.getTitle(), 1, dto.getTotal(), dto.getTotal(),
                serviceUrl + "/api/v1/payment/success/"+paymentCode, serviceUrl + "/api/v1/payment/cancel/"+paymentCode,
                serviceUrl + "/api/v1/payment/failure/"+paymentCode);


        HttpEntity<KakaoReadyRequest> request = new HttpEntity<>(kakaoRequest, headers);
        KakaoPayResponse result = restTemplate.postForObject(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                request,
                KakaoPayResponse.class
        );

        byte[] encryptByte = aesBytesEncryptor.encrypt(result.getTid().getBytes(StandardCharsets.UTF_8));
        String tid = Base64.getEncoder().encodeToString(encryptByte);

        OrderMessageDto message = new OrderMessageDto(dto.getProjectIdx(), memberDto.getMemberIdx()
                , dto.getTotal(), dto.getMessage(), tid, paymentCode);

        RBucket<OrderMessageDto> ordersBucket = redissonClient.getBucket("order::" + paymentCode);

        ordersBucket.set(message, Duration.ofMinutes(10L));

        return result;
    }

    @Transactional
    public void kakaoPaymentAccess(OrderMessageDto dto){

        RBucket<ProjectCache> projectsRBucket = redissonClient.getBucket("projects::"+dto.getProjectIdx());
        ProjectCache projects = projectsRBucket.get();
        if(projects.getGoalAmount() < projects.getCurrentAmount() + dto.getTotal()){
            throw new OrderException("목표 금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);
        }else{
            projects.setCurrentAmount(projects.getCurrentAmount() + dto.getTotal());
            projects.setParticipantsCount(projects.getParticipantsCount()+1);
            projectsRBucket.set(projects);
        }

    }

    @Transactional
    public void kakaoPayRollBack(OrderMessageDto dto){
        RBucket<ProjectCache> projectsRBucket = redissonClient.getBucket("projects::"+dto.getProjectIdx());
        ProjectCache projects = projectsRBucket.get();
        projects.setCurrentAmount(projects.getCurrentAmount() - dto.getTotal());
        projects.setParticipantsCount(projects.getParticipantsCount()-1);
        projectsRBucket.set(projects);
    }

    @Transactional
    public void cancelOrder(String paymentCode){
        orderRepository.deleteOrdersWithPaymentCode(paymentCode);
    }

}
