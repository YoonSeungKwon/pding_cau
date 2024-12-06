package yoon.capstone.application.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderDto {

    private long projectIdx;

    private int total;

    private String message;

}
