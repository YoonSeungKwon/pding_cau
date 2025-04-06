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
            boolean available = cacheManager.available("order::"+id, 5L); //Try Lock
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
            boolean available = cacheManager.available("project::"+projects.getProjectIdx(), 5L);
            if(!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            cacheManager.cachePut("project", String.valueOf(projects.getProjectIdx()), projects);

        }catch (Exception e){
            rollbackOrder(dto);
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {  //UnLock
            if(cacheManager.checkLock("project::"+id))
                cacheManager.unlock("project::"+id);
        }

        try {   //Publish Message
            messageManager.publish(dto);
        }catch (Exception e){
            rollbackCache(dto);
            rollbackOrder(dto);
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


    public void rollbackOrder(OrderMessageDto dto){
        try {
            boolean available = cacheManager.available("order::"+dto.getPaymentCode());
            if (!available)//로깅, 서버 비정상 종료시 대처법
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            orderService.deleteOrder(dto.getPaymentCode()); // 트랜잭션

        }catch (Exception e){
            System.out.println(e.getMessage());
            log.error("주문 데이터 롤백 실패 " +
                    "\n projectIndex " + dto.getProjectIdx() +
                    "\n total        " + dto.getTotal());
        }finally {
            if(cacheManager.checkLock("order::"+dto.getPaymentCode()))
                cacheManager.unlock("order::"+dto.getPaymentCode());
        }

        throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public void rollbackCache(OrderMessageDto dto){
        try {
            boolean available = cacheManager.available("project::"+dto.getProjectIdx());
            if(!available)//로깅, 서버 비정상 종료시 대처법
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            Projects projects = cacheManager.cacheGet("project", String.valueOf(dto.getProjectIdx()), Projects.class);
            if (projects == null) {
                return;
            }

            projects.setCurrentAmount(projects.getCurrentAmount() - dto.getTotal());
            projects.setParticipantsCount(projects.getParticipantsCount() - 1);

            cacheManager.cachePut("project", String.valueOf(dto.getProjectIdx()), projects);
        }catch (Exception e){
            System.out.println(e.getMessage());
            log.error("캐시 데이터 롤백 실패 " +
                    "\n projectIndex " + dto.getProjectIdx() +
                    "\n total        " + dto.getTotal());
        }finally {
            if(cacheManager.checkLock("project::"+dto.getProjectIdx()))
                cacheManager.unlock("project::"+dto.getProjectIdx());
        }
    }


}
