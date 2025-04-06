package yoon.capstone.application.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yoon.capstone.application.common.dto.request.OrderDto;
import yoon.capstone.application.common.dto.response.KakaoPayResponse;
import yoon.capstone.application.common.dto.response.OrderResponse;
import yoon.capstone.application.service.OrderService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<String> order(@RequestBody OrderDto dto){

        KakaoPayResponse result = orderService.kakaoPayment(dto);

        return new ResponseEntity<>(result.getNext_redirect_pc_url(), HttpStatus.OK);
    }

    @GetMapping("/{idx}")
    public ResponseEntity<List<OrderResponse>> getList(@PathVariable long idx){
        List<OrderResponse> result = orderService.getOrderList(idx);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



}
