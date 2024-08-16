package yoon.capstone.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderMessageDto {

    private long projectIdx;

    private long memberIdx;

    private int total;

    private String message;

    private String tid;

    private String paymentCode;

}
