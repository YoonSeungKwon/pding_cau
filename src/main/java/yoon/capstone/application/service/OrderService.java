package yoon.capstone.application.service;

import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.dto.request.MemberSecurityDto;
import yoon.capstone.application.entity.Members;
import yoon.capstone.application.entity.Orders;
import yoon.capstone.application.entity.Payment;
import yoon.capstone.application.entity.Projects;
import yoon.capstone.application.dto.request.OrderDto;
import yoon.capstone.application.dto.response.KakaoPayResponse;
import yoon.capstone.application.dto.response.KakaoResultResponse;
import yoon.capstone.application.dto.response.OrderResponse;
import yoon.capstone.application.enums.ExceptionCode;
import yoon.capstone.application.exception.OrderException;
import yoon.capstone.application.exception.UnauthorizedException;
import yoon.capstone.application.repository.MemberRepository;
import yoon.capstone.application.repository.OrderRepository;
import yoon.capstone.application.repository.PaymentRepository;
import yoon.capstone.application.repository.ProjectsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${KAKAOPAY_KEY}")
    private String admin_key;
    @Value("${SERVICE_URL}")
    private String serviceUrl;

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    private final ProjectsRepository projectsRepository;

    private OrderResponse toResponse(Orders orders){
        return new OrderResponse(orders.getMembers().getUsername(), orders.getMembers().getProfile(),
                orders.getPayment().getTotal(), orders.getMessage(), orders.getPayment().getCreatedAt());
    }

    public List<OrderResponse> getOrderList(long idx){
        Projects tempProject = projectsRepository.findProjectsByProjectIdx(idx);
        List<Orders> list = orderRepository.findAllByProjects(tempProject);
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

        MemberSecurityDto memberDto = (MemberSecurityDto) authentication.getPrincipal();
        Members currentMember = memberRepository.findMembersByMemberIdx(memberDto.getMemberIdx());

        Projects projects = projectsRepository.findProjectsByProjectIdx(dto.getProjectIdx());

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

        Payment payment = Payment.builder()
                .paymentCode(paymentCode)
                .members(currentMember)
                .product(projects.getTitle())
                .quantity(1)
                .total(dto.getTotal())
                .tid(result.getTid())
                .createdAt(result.getCreated_at())
                .build();

        Orders orders = Orders.builder()
                .projects(projects)
                .members(currentMember)
                .payment(payment)
                .message(dto.getMessage())
                .build();

        paymentRepository.save(payment);
        orderRepository.save(orders);

        return result;
    }

    @Transactional
    public long kakaoPaymentAccess(String id, String token){

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "KakaoAK " + admin_key);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();


        Payment payment = paymentRepository.findPaymentByPaymentCode(id);
        if(payment == null) {
            throw new OrderException(ExceptionCode.ORDER_NOT_FOUND.getMessage(), ExceptionCode.ORDER_NOT_FOUND.getStatus());
        }
        try {
            Projects projects = projectsRepository.findProjectsByTitle(payment.getProduct());
            if (projects == null) {
                throw new OrderException(ExceptionCode.ORDER_NOT_FOUND.getMessage(), ExceptionCode.ORDER_NOT_FOUND.getStatus());
            }


            map.add("cid", "TC0ONETIME");
            map.add("tid", payment.getTid());
            map.add("partner_order_id", payment.getPaymentCode());
            map.add("partner_user_id", payment.getMembers().getUsername());
            map.add("pg_token", token);



            projects.setCurrentAmount(projects.getCurrentAmount() + payment.getTotal());  //Lock  필요
            projects.setParticipantsCount(projects.getParticipantsCount() + 1);                   //Lock  필요

            projectsRepository.save(projects);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers); //Lock 처리 후 결제완료 요청

            KakaoResultResponse result = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/approve",
                    request,
                    KakaoResultResponse.class
            );

            return projects.getProjectIdx();

        }catch (LockTimeoutException e){
            throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());
        }
    }

    @Transactional
    public void cancelOrder(String orderId){
        Payment payment = paymentRepository.findPaymentByPaymentCode(orderId);
        Orders orders = orderRepository.findOrdersByPayment(payment);

        if(orders!= null){
            orderRepository.delete(orders);
            if(payment != null)
                paymentRepository.delete(payment);
        }


    }


}
