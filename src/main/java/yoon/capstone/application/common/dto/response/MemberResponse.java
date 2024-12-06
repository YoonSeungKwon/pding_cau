package yoon.capstone.application.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MemberResponse {

    private long memberIdx;

    private String email;

    private String name;

    private String phone;

    private String profile;

    private boolean oauth;

    private LocalDateTime lastVisit;
}
