package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Cache;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.common.dto.request.KakaoApproveRequest;
import yoon.capstone.application.common.dto.response.KakaoResultResponse;
import yoon.capstone.application.common.dto.response.OrderMessageDto;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.OrderException;
import yoon.capstone.application.common.util.AesEncryptorManager;
import yoon.capstone.application.service.manager.CacheManager;
import yoon.capstone.application.service.manager.KakaoOrderManager;
import yoon.capstone.application.service.manager.MessageManager;
import yoon.capstone.application.service.manager.OrderManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderFacade {

    private final AesEncryptorManager aesEncryptorManager;

    private final OrderManager orderManager;

    private final MessageManager messageManager;

    private final OrderService orderService;

    private final RedissonClient redissonClient;

    private final CacheManager cacheManager;

    public void order(String id, String token) {


        RBucket<OrderMessageDto> orderBucket = redissonClient.getBucket("order::" + id);
        OrderMessageDto dto = orderBucket.get();


        try {
            boolean available = cacheManager.available("order::"+id);//rLock.tryLock(60L, 1L, TimeUnit.SECONDS);
            if (!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            orderService.kakaoPaymentAccess(dto); // 트랜잭션

        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            if(cacheManager.checkLock("order::"+id))
                cacheManager.unlock("order::"+id);
        }
        sendKakaoApproveRequest(dto, token);
        messageManager.publish(dto);
    }

    public void sendKakaoApproveRequest(OrderMessageDto dto, String token){
        String tid = aesEncryptorManager.decode(dto.getTid());
        try {
            orderManager.orderAccess(dto.getMemberIdx(), dto.getPaymentCode(), tid, token);
        }catch (Exception e) {
            rollbackOrder(e, dto);
        }
    }


    public void rollbackOrder(Exception e, OrderMessageDto dto){
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
