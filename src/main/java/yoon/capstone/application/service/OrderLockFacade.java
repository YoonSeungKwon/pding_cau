package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.dto.request.KakaoApproveRequest;
import yoon.capstone.application.dto.response.KakaoResultResponse;
import yoon.capstone.application.dto.response.OrderMessageDto;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.OrderException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderLockFacade {

    @Value("${KAKAOPAY_KEY}")
    private String admin_key;
    @Value("${SERVICE_URL}")
    private String serviceUrl;
    @Value("${RABBITMQ_EXCHANGE_NAME}")
    private String exchange;
    @Value("${RABBITMQ_ROUTING_KEY}")
    private String routingKey;

    private final AesBytesEncryptor aesBytesEncryptor;

    private final OrderService orderService;

    private final RedissonClient redissonClient;

    private final RabbitTemplate rabbitTemplate;

    public void order(String id, String token) {
        RBucket<OrderMessageDto> orderBucket = redissonClient.getBucket("order::" + id);
        OrderMessageDto dto = orderBucket.get();

        RLock rLock = redissonClient.getLock("projects" + dto.getProjectIdx());

        try {
            boolean available = rLock.tryLock(60L, 1L, TimeUnit.SECONDS);
            if (!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            orderService.kakaoPaymentAccess(dto); // 트랜잭션

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            if(rLock.isHeldByCurrentThread())
                rLock.unlock();
        }
        sendKakaoApproveRequest(dto, token);
        publishMessage(dto);
    }

    public void sendKakaoApproveRequest(OrderMessageDto dto, String token){
        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.set("Content-type", "application/json");
        headers.set("Authorization", "DEV_SECRET_KEY " + admin_key);
        System.out.println(token);
        byte[] byteTid = Base64.getDecoder().decode(dto.getTid());
        String tid = new String(aesBytesEncryptor.decrypt(byteTid), StandardCharsets.UTF_8);

        try {
            KakaoApproveRequest kakaoApproveRequest = new KakaoApproveRequest("TC0ONETIME", tid,
                    dto.getPaymentCode(), String.valueOf(dto.getMemberIdx()), token);

            HttpEntity<KakaoApproveRequest> request = new HttpEntity<>(kakaoApproveRequest, headers);

            KakaoResultResponse response = restTemplate.postForObject(
                    "https://open-api.kakaopay.com/online/v1/payment/approve",
                    request,
                    KakaoResultResponse.class
            );
            System.out.println(response);
            //RestTemplate Exception MQ 발행 전(Redis Rollback)
        }catch (HttpClientErrorException e) {      //4xx
            rollbackPayment(e, dto);
        }catch (HttpServerErrorException e) {      //5xx
            rollbackPayment(e, dto);
        }catch (RestClientException e) {           //other
            rollbackPayment(e, dto);
        }catch (Exception e){                      //server side
            rollbackPayment(e, dto);
        }
    }

    public void publishMessage(OrderMessageDto dto){
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, dto);
        }catch (AmqpException e) {                  //rabbit
            System.out.println("Message sending failed: " + e.getMessage());
            log.error("메시지큐 전송 실패" +
                    "\n memberIndex  " + dto.getMemberIdx() +
                    "\n projectIndex " + dto.getProjectIdx() +
                    "\n paymentCode  " + dto.getPaymentCode() +
                    "\n total        " + dto.getTotal() +
                    "\n tid          " + dto.getTid() +
                    "\n message      " + dto.getMessage());
        }
    }

    public void rollbackPayment(Exception e, OrderMessageDto dto){
        System.out.println("Client error: " + e.getMessage());


        RLock rLock = redissonClient.getLock("projects" + dto.getProjectIdx());
        try {
            boolean available = rLock.tryLock(1000L, 5L, TimeUnit.SECONDS);
            if (!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            orderService.kakaoPayRollBack(dto); // 트랜잭션

        }catch (Exception ex){
            System.out.println(ex.getMessage());
            log.error("Redis 롤백 실패 " +
                    "\n projectIndex " + dto.getProjectIdx() +
                    "\n total        " + dto.getTotal());
        }finally {
            if(rLock.isHeldByCurrentThread())
                rLock.unlock();
        }

        throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
