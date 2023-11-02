package yoon.capstone.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import yoon.capstone.application.domain.Members;
import yoon.capstone.application.domain.Payment;
import yoon.capstone.application.repository.PaymentRepository;
import yoon.capstone.application.vo.response.KakaoPayResponse;
import yoon.capstone.application.vo.response.KakaoResultResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${kakao.pay.key}")
    private String admin_key;

    private final PaymentRepository paymentRepository;

    private Payment payment;
    public KakaoPayResponse kakaoPayment(){

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();
        
        String orderId = UUID.randomUUID().toString();
        Members members = (Members) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "KakaoAK " + admin_key);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("cid", "TC0ONETIME");
        map.add("partner_order_id", orderId);
        map.add("partner_user_id", members.getEmail());
        map.add("item_name", "Fund");
        map.add("quantity", 1);
        map.add("total_amount", 10000);
        map.add("tax_free_amount", 10000);
        map.add("approval_url", "http://localhost:8080/api/v1/payment/success");
        map.add("cancel_url", "http://localhost:8080/api/v1/payment/cancel");
        map.add("fail_url", "http://localhost:8080/api/v1/payment/failure");

        HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(map, headers);
        KakaoPayResponse result = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
                request,
                KakaoPayResponse.class
        );

        Payment payment = Payment.builder()
                .orderId(orderId)
                .members(members)
                .itemName("funds")
                .quantity(1)
                .total(10000)
                .tid(result.getTid())
                .regdate(result.getCreated_at())
                .build();
        
        paymentRepository.save(payment);
        this.payment = payment;

        return result;
    }

    public KakaoResultResponse kakaoPaymentAccess(String token){

        HttpHeaders headers = new HttpHeaders();
        RestTemplate restTemplate = new RestTemplate();


        headers.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.set("Authorization", "KakaoAK " + admin_key);

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        map.add("cid","TC0ONETIME");
        map.add("tid", this.payment.getTid());
        map.add("partner_order_id", this.payment.getOrderId());
        map.add("partner_user_id", this.payment.getMembers().getEmail());
        map.add("pg_token", token);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);

        KakaoResultResponse result = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/approve",
                request,
                KakaoResultResponse.class
        );

        return result;
    }

}
