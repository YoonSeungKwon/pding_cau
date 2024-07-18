package yoon.capstone.application.service;

import jakarta.persistence.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
import yoon.capstone.application.dto.response.KakaoPayResponse;
import yoon.capstone.application.dto.response.KakaoResultResponse;
import yoon.capstone.application.dto.response.OrderResponse;
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

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    private final ProjectsRepository projectsRepository;

    private OrderResponse toResponse(Orders orders){
        return new OrderResponse(orders.getMembers().getUsername(), orders.getMembers().getProfile(),
                orders.getPayment().getCost(), orders.getComments().getContent(), orders.getPayment().getCreatedAt());
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
        //Lazy Loading
        Members currentMember = memberRepository.findMembersByMemberIdx(memberDto.getMemberIdx()).orElseThrow(()->new UnauthorizedException(ExceptionCode.UNAUTHORIZED_ACCESS));

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

        byte[] encryptByte = aesBytesEncryptor.encrypt(result.getTid().getBytes(StandardCharsets.UTF_8));
        String tid = Base64.getEncoder().encodeToString(encryptByte);

        Payment payment = Payment.builder()
                .paymentCode(paymentCode)
                .tid(tid)
                .cost(dto.getTotal())
                .createdAt(result.getCreated_at())
                .build();

        Comments comments = Comments.builder()
                .content(dto.getMessage())
                .build();

        Orders orders = Orders.builder()
                .projects(projects)
                .members(currentMember)
                .payment(payment)
                .build();


        comments.setOrders(orders);

        orderRepository.save(orders);

        return result;
    }

    @Transactional
    public void kakaoPaymentAccess(String id, String token){

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "KakaoAK " + admin_key);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        Orders orders = orderRepository.findOrdersByPaymentCodeWithFetchJoin(id).orElseThrow(()->new OrderException(ExceptionCode.ORDER_NOT_FOUND.getMessage(),
                ExceptionCode.ORDER_NOT_FOUND.getStatus()));

        try {

            Projects projects = projectsRepository.findProjectsByOrderIndexWithLock(orders.getOrderIdx()).orElseThrow(
                    ()-> new OrderException(ExceptionCode.ORDER_NOT_FOUND.getMessage(), ExceptionCode.ORDER_NOT_FOUND.getStatus())
            ); //Lock

            map.add("cid", "TC0ONETIME");
            map.add("tid", orders.getPayment().getTid());
            map.add("partner_order_id", orders.getPayment().getPaymentCode());
            map.add("partner_user_id", orders.getMembers().getUsername());
            map.add("pg_token", token);


            projects.addAmount(orders.getPayment().getCost());


            projectsRepository.save(projects);

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers); //Lock 처리 후 결제완료 요청

            KakaoResultResponse result = restTemplate.postForObject(
                    "https://kapi.kakao.com/v1/payment/approve",
                    request,
                    KakaoResultResponse.class
            );

        }catch (LockTimeoutException e){
            orderRepository.delete(orders);    //주문 정보 삭제
            throw new OrderException(ExceptionCode.ORDER_LOCK_TIMEOUT.getMessage(), ExceptionCode.ORDER_LOCK_TIMEOUT.getStatus());
        }
    }

    @Transactional
    public void cancelOrder(String paymentCode){
        orderRepository.deleteOrdersWithPaymentCode(paymentCode);
    }


}
