package yoon.capstone.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderMessageDto {

    private long projectIdx;

    private long memberIdx;

    private int total;

    private String message;

    private String tid;

    private String paymentCode;

}
