package yoon.capstone.application.service;

import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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
    @Value("${RABBITMQ_EXCHANGE_NAME}")
    private String exchange;
    @Value("${RABBITMQ_ROUTING_KEY}")
    private String routingKey;

    private final AesBytesEncryptor aesBytesEncryptor;

    private final OrderRepository orderRepository;

    private final ProjectsRepository projectsRepository;

    private final RabbitTemplate rabbitTemplate;

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
            rBucket.set(toCache(projects), Duration.ofMinutes(10L));
        }
        //

        ProjectCache projects = rBucket.get();

        if(projects.getCurrentAmount() + dto.getTotal() > projects.getGoalAmount())
            throw new OrderException("목표금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);



        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "KakaoAK " + admin_key);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("cid", "TC0ONETIME");
        map.add("partner_order_id", paymentCode);
        map.add("partner_user_id", memberDto.getEmail());
        map.add("item_name", projects.getTitle());
        map.add("quantity", 1);
        map.add("total_amount", dto.getTotal());
        map.add("tax_free_amount", dto.getTotal());
        map.add("approval_url", serviceUrl + "/api/v1/payment/success/"+paymentCode);
        map.add("cancel_url", serviceUrl + "/api/v1/payment/cancel/"+paymentCode);
        map.add("fail_url", serviceUrl + "/api/v1/payment/failure/"+paymentCode);

        HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(map, headers);
        KakaoPayResponse result = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
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
    public void kakaoPaymentAccess(String id, String token){

        RBucket<OrderMessageDto> orderBucket = redissonClient.getBucket("order::"+id);
        OrderMessageDto dto = orderBucket.get();

        //RLock
        RLock rLock = redissonClient.getLock("projects"+dto.getProjectIdx());
        try {
            boolean available = rLock.tryLock(60L, 1L, TimeUnit.SECONDS);
            if(!available) throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            RBucket<ProjectCache> projectsRBucket = redissonClient.getBucket("projects::"+dto.getProjectIdx());
            ProjectCache projects = projectsRBucket.get();
            if(projects.getGoalAmount() < projects.getCurrentAmount() + dto.getTotal()){
                throw new OrderException("목표 금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);
            }else{
                projects.setCurrentAmount(projects.getCurrentAmount() + dto.getTotal());
                projects.setParticipantsCount(projects.getParticipantsCount()+1);
                projectsRBucket.set(projects);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            if(rLock.isHeldByCurrentThread())
                rLock.unlock();
        }

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "KakaoAK " + admin_key);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        byte[] byteTid = Base64.getDecoder().decode(dto.getTid());
        String tid = new String(aesBytesEncryptor.decrypt(byteTid), StandardCharsets.UTF_8);

        try {

            map.add("cid", "TC0ONETIME");
            map.add("tid", tid);
            map.add("partner_order_id", dto.getPaymentCode());
            map.add("partner_user_id", dto.getMemberIdx());
            map.add("pg_token", token);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers); //Lock 처리 후 결제완료 요청

            restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/approve",
                    request,
                    KakaoResultResponse.class
            );


        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        rabbitTemplate.convertAndSend(exchange, routingKey, dto);
    }

    @Transactional
    public void cancelOrder(String paymentCode){
        orderRepository.deleteOrdersWithPaymentCode(paymentCode);
    }


}
