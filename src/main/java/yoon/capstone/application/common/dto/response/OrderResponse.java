package yoon.capstone.application.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class OrderResponse {

    private String member;

    private String profile;

    private int pay;

    private String message;

    private LocalDateTime regdate;

}
