package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import yoon.capstone.application.service.OrderService;
import yoon.capstone.application.vo.response.KakaoResultResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final OrderService orderService;

    @GetMapping("/success")
    public RedirectView paymentSuccessHandler(@RequestParam("pg_token") String token){
        orderService.kakaoPaymentAccess(token);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("http://13.124.127.93:3000/success/");
        return redirectView;
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