package yoon.capstone.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import yoon.capstone.application.service.OrderService;
import yoon.capstone.application.vo.request.OrderDto;
import yoon.capstone.application.vo.response.KakaoPayResponse;
import yoon.capstone.application.vo.response.OrderResponse;

import java.util.List;

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

    @GetMapping("/{idx}")
    public ResponseEntity<List<OrderResponse>> getList(@PathVariable long idx){
        List<OrderResponse> result = orderService.getOrderList(idx);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
