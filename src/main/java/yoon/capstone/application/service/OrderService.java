package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.common.annotation.Authenticated;
import yoon.capstone.application.common.dto.request.KakaoReadyRequest;
import yoon.capstone.application.common.dto.request.OrderDto;
import yoon.capstone.application.common.dto.response.KakaoPayResponse;
import yoon.capstone.application.common.dto.response.OrderMessageDto;
import yoon.capstone.application.common.dto.response.OrderResponse;
import yoon.capstone.application.common.dto.response.ProjectCache;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.OrderException;
import yoon.capstone.application.common.exception.ProjectException;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.service.domain.Orders;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.service.repository.OrderRepository;
import yoon.capstone.application.service.repository.ProjectRepository;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${KAKAOPAY_KEY}")
    private String admin_key;
    @Value("${SERVICE_URL}")
    private String serviceUrl;

    private final AesBytesEncryptor aesBytesEncryptor;

    private final OrderRepository orderRepository;

    private final ProjectRepository projectsRepository;

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
        List<Orders> list = orderRepository.findAllOrders(idx);
        List<OrderResponse> result = new ArrayList<>();
        for(Orders o:list){
            result.add(toResponse(o));
        }
        return result;
    }

    @Transactional
    @Authenticated
    public KakaoPayResponse kakaoPayment(OrderDto dto){

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();

        String paymentCode = UUID.randomUUID().toString();

        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //Read Cache Or Cache Warm
        RBucket<ProjectCache> rBucket = redissonClient.getBucket("projects::" + dto.getProjectIdx());
        if (rBucket.get() == null) {
            Projects projects = projectsRepository.findProject(dto.getProjectIdx()).orElseThrow(()->new ProjectException(ExceptionCode.PROJECT_NOT_FOUND));
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
        orderRepository.cancelOrder(paymentCode);
    }

}
