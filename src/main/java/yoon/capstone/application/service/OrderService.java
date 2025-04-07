package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yoon.capstone.application.common.annotation.Authenticated;
import yoon.capstone.application.common.dto.request.OrderDto;
import yoon.capstone.application.common.dto.response.KakaoPayResponse;
import yoon.capstone.application.common.dto.response.OrderMessageDto;
import yoon.capstone.application.common.dto.response.OrderResponse;
import yoon.capstone.application.common.dto.response.ProjectCache;
import yoon.capstone.application.common.enums.ExceptionCode;
import yoon.capstone.application.common.exception.OrderException;
import yoon.capstone.application.common.exception.ProjectException;
import yoon.capstone.application.common.util.AesEncryptorManager;
import yoon.capstone.application.config.security.JwtAuthentication;
import yoon.capstone.application.service.domain.Orders;
import yoon.capstone.application.service.domain.Projects;
import yoon.capstone.application.service.manager.CacheManager;
import yoon.capstone.application.service.manager.OrderManager;
import yoon.capstone.application.service.repository.OrderRepository;
import yoon.capstone.application.service.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {


    private final AesEncryptorManager aesEncryptorManager;

    private final OrderRepository orderRepository;

    private final ProjectRepository projectsRepository;

    private final OrderManager orderManager;

    private final CacheManager cacheManager;

    private OrderResponse toResponse(Orders orders){
        return new OrderResponse(orders.getMembers().getUsername(), orders.getMembers().getProfile(),
                orders.getPayment().getCost(), orders.getComments().getContent(), orders.getPayment().getCreatedAt());
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
    public KakaoPayResponse kakaoPayment(OrderDto dto){     //카카오 결제 준비

        String paymentCode = UUID.randomUUID().toString();

        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Projects projects;

        try{
            boolean available = cacheManager.available("project::"+dto.getProjectIdx(), 5L);
            if(!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            projects = cacheManager.cacheGet("project", String.valueOf(dto.getProjectIdx()), Projects.class);
            if (projects == null) {
                projects = projectsRepository.findProject(dto.getProjectIdx()).orElseThrow(()->new ProjectException(ExceptionCode.PROJECT_NOT_FOUND));
                cacheManager.cachePut("project", String.valueOf(dto.getProjectIdx()), projects);
            }

        }catch (Exception e) {
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            if(cacheManager.checkLock("project::"+dto.getProjectIdx()))
                cacheManager.unlock("project::"+dto.getProjectIdx());
        }

        if(projects.getCurrentAmount() + dto.getTotal() > projects.getGoalAmount())
            throw new OrderException("목표금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);

        KakaoPayResponse result = (KakaoPayResponse) orderManager.orderPrepare(memberDto.getMemberIdx(), projects.getTitle(), paymentCode, dto.getTotal());

        String tid = aesEncryptorManager.encode(result.getTid());

        OrderMessageDto message = new OrderMessageDto(dto.getProjectIdx(), memberDto.getMemberIdx()
                , dto.getTotal(), dto.getMessage(), tid, paymentCode);

        cacheManager.cachePut("order", paymentCode, message);

        return result;
    }

    @Transactional
    public Projects kakaoPaymentAccess(OrderMessageDto dto, String token){      //카카오 결제 승인
        Projects projects;
        try{
            boolean available = cacheManager.available("project::"+dto.getProjectIdx(), 5L);
            if(!available) {
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());
            }

            //Load Project For Validation
            projects = cacheManager.cacheGet("project", String.valueOf(dto.getProjectIdx()), Projects.class);

            if(projects == null){   //Cache Miss Access Database
                projects = projectsRepository.findProject(dto.getProjectIdx())
                        .orElseThrow(()->new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus()));
            }

            //Amount Validation
            if(projects.getGoalAmount() < projects.getCurrentAmount() + dto.getTotal()){
                throw new OrderException("목표 금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);
            }else{

                //Order Request
                String tid = aesEncryptorManager.decode(dto.getTid());
                orderManager.orderAccess(dto.getMemberIdx(), dto.getPaymentCode(), tid, token);

                //Update Project
                projects.setCurrentAmount(projects.getCurrentAmount() + dto.getTotal());
                projects.setParticipantsCount(projects.getParticipantsCount()+1);
            }

        }catch (Exception e) {
            System.out.println(e.getMessage());
            deleteOrder(dto.getPaymentCode());//Delete Order Try Data From Database
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {  //Project Cache Unlock
            if (cacheManager.checkLock("project::" + dto.getProjectIdx()))
                cacheManager.unlock("project::" + dto.getProjectIdx());
        }

        return projects;
    }


    @Transactional
    public void deleteOrder(String paymentCode){
        orderRepository.cancelOrder(paymentCode);
    }

}
