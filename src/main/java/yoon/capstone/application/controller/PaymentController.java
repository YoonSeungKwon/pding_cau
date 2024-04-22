package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import yoon.capstone.application.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final OrderService orderService;

    @Value("${PAYMENT_SUCCESS_URL}")
    private String redirectUrl;

    @GetMapping("/success/{id}")
    public RedirectView paymentSuccessHandler(@PathVariable String id, @RequestParam("pg_token") String token){
        orderService.kakaoPaymentAccess(id, token);
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(redirectUrl);
        return redirectView;
    }

    @GetMapping("/cancel/{id}")
    public String paymentCancelHandler(@PathVariable String id){
        orderService.cancelOrder(id);
        return "canceled";
    }

    @GetMapping("/failure/{id}")
    public String paymentFailureHandler(@PathVariable String id){
        orderService.cancelOrder(id);
        return "failed";
    }

}

//결제완료 response 간편화, 유저 인계 고려, 주문번호 고려 필요