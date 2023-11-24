package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yoon.capstone.application.service.OrderService;
import yoon.capstone.application.vo.request.OrderDto;
import yoon.capstone.application.vo.response.KakaoPayResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;


    @PostMapping("/")
    public ResponseEntity<String> payment(@RequestBody OrderDto dto){

        KakaoPayResponse result = orderService.kakaoPayment(dto);

        return new ResponseEntity<>(result.getNext_redirect_pc_url(), HttpStatus.OK);
    }

}
