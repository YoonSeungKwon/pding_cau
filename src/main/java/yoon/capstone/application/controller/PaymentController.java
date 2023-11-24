package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yoon.capstone.application.service.OrderService;
import yoon.capstone.application.vo.response.KakaoResultResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final OrderService orderService;

    @GetMapping("/success")
    public void paymentSuccessHandler(@RequestParam("pg_token") String token){
        KakaoResultResponse result = orderService.kakaoPaymentAccess(token);
    }

    @GetMapping("/cancel")
    public String paymentCancelHandler(){
        return "canceled";
    }

    @GetMapping("/failure")
    public String paymentFailureHandler(){
        return "failed";
    }

}

//결제완료 response 간편화, 유저 인계 고려, 주문번호 고려 필요