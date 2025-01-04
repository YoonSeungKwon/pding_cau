package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
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

    private final RedissonClient redissonClient;

    private final OrderManager orderManager;

    private final CacheManager cacheManager;

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

        String paymentCode = UUID.randomUUID().toString();

        JwtAuthentication memberDto = (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Projects projects;

        try{
            boolean available = cacheManager.available("projects::"+dto.getProjectIdx());
            if(!available)
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());

            projects = cacheManager.cacheGet("projects", String.valueOf(dto.getProjectIdx()), Projects.class);
            if (projects == null) {
                projects = projectsRepository.findProject(dto.getProjectIdx()).orElseThrow(()->new ProjectException(ExceptionCode.PROJECT_NOT_FOUND));
                cacheManager.cachePut("projects", String.valueOf(dto.getProjectIdx()), projects);
            }

        }catch (Exception e) {
            System.out.println(e.getMessage());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            if(cacheManager.checkLock("projects::"+dto.getProjectIdx()))
                cacheManager.unlock("projects::"+dto.getProjectIdx());
        }

        if(projects.getCurrentAmount() + dto.getTotal() > projects.getGoalAmount())
            throw new OrderException("목표금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);

        KakaoPayResponse result = (KakaoPayResponse) orderManager.orderPrepare(memberDto.getMemberIdx(), projects.getTitle(), paymentCode, dto.getTotal());

        String tid = aesEncryptorManager.encode(result.getTid());

        OrderMessageDto message = new OrderMessageDto(dto.getProjectIdx(), memberDto.getMemberIdx()
                , dto.getTotal(), dto.getMessage(), tid, paymentCode);

        cacheManager.cachePut("orders", paymentCode, message);

        return result;
    }

    @Transactional
    public void kakaoPaymentAccess(OrderMessageDto dto){

        try{
            boolean available = cacheManager.available("projects::"+dto.getProjectIdx());
            if(!available) {
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());
            }

            Projects projects = cacheManager.cacheGet("projects", String.valueOf(dto.getProjectIdx()), Projects.class);

            if(projects == null){
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());
            }


            if(projects.getGoalAmount() < projects.getCurrentAmount() + dto.getTotal()){
                throw new OrderException("목표 금액을 초과하였습니다.", HttpStatus.BAD_REQUEST);
            }else{
                projects.setCurrentAmount(projects.getCurrentAmount() + dto.getTotal());
                projects.setParticipantsCount(projects.getParticipantsCount()+1);
                cacheManager.cachePut("projects", String.valueOf(projects.getProjectIdx()), projects);
            }

        }catch (Exception e) {
            System.out.println(e.getMessage());
            cancelOrder(dto.getPaymentCode());
            throw new OrderException("결제에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            if (cacheManager.checkLock("projects::" + dto.getProjectIdx()))
                cacheManager.unlock("projects::" + dto.getProjectIdx());
        }
    }

    @Transactional
    public void kakaoPayRollBack(OrderMessageDto dto){

        try {
            boolean available = cacheManager.available("projects::"+dto.getProjectIdx());
            if(!available) {
                throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());
            }

            Projects projects = cacheManager.cacheGet("projects", String.valueOf(dto.getProjectIdx()), Projects.class);

            projects.setCurrentAmount(projects.getCurrentAmount() - dto.getTotal());
            projects.setParticipantsCount(projects.getParticipantsCount() - 1);

            cacheManager.cachePut("projects", String.valueOf(dto.getProjectIdx()), projects);
        }catch (Exception e){
        }finally {
            if (cacheManager.checkLock("projects::" + dto.getProjectIdx()))
                cacheManager.unlock("projects::" + dto.getProjectIdx());
        }
    }

    @Transactional
    public void cancelOrder(String paymentCode){
        orderRepository.cancelOrder(paymentCode);
    }

}
