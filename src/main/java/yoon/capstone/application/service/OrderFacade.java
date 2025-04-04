package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import yoon.capstone.application.common.dto.response.OrderMessageDto;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.OrderException;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.service.manager.CacheManager;
import yoon.capstone.application.service.manager.MessageManager;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderFacade {

    private final MessageManager messageManager;

    private final OrderService orderService;

    private final CacheManager cacheManager;

    public void order(String id, String token) {


        OrderMessageDto dto = cacheManager.cacheGet("order", id, OrderMessageDto.class);
        Projects projects;

        try {   //PG사 결제
            boolean available = cacheManager.available("order::"+id); //Try Lock
            if (!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());
            projects = orderService.kakaoPaymentAccess(dto, token);
        }catch (Exception e){   //결제 에러
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {  //UnLock
            if(cacheManager.checkLock("order::"+id))
                cacheManager.unlock("order::"+id);
        }

        try{    //Entity Caching
            boolean available = cacheManager.available("projects::"+projects.getProjectIdx());
            if(!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            cacheManager.cachePut("projects", String.valueOf(projects.getProjectIdx()), projects);

        }catch (Exception e){
            rollbackOrder(e, dto);
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {  //UnLock
            if(cacheManager.checkLock("projects::"+id))
                cacheManager.unlock("projects::"+id);
        }

        try {   //Publish Message
            messageManager.publish(dto);
        }catch (Exception e){
            rollbackCache();
            rollbackOrder(e, dto);
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {

        }


    }


    public void rollbackOrder(Exception e, OrderMessageDto dto){
        System.out.println("Client error: " + e.getMessage());

        try {
            boolean available = cacheManager.available("order::"+dto.getPaymentCode());
            if (!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            orderService.kakaoPayRollBack(dto); // 트랜잭션

        }catch (Exception ex){
            System.out.println(ex.getMessage());
            log.error("Redis 롤백 실패 " +
                    "\n projectIndex " + dto.getProjectIdx() +
                    "\n total        " + dto.getTotal());
        }finally {
            if(cacheManager.checkLock("order::"+dto.getPaymentCode()))
                cacheManager.unlock("order::"+dto.getPaymentCode());
        }

        throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void rollbackCache(){

    }


}
