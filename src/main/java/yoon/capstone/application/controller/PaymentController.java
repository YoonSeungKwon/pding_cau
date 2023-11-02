package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yoon.capstone.application.service.PaymentService;
import yoon.capstone.application.vo.response.KakaoResultResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/success")
    public ResponseEntity<KakaoResultResponse> paymentSuccessHandler(@RequestParam("pg_token") String token){
        KakaoResultResponse result = paymentService.kakaoPaymentAccess(token);
        return new ResponseEntity<>(result, HttpStatus.OK);
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
