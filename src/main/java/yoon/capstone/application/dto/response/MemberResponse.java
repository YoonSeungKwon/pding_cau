package yoon.capstone.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class MemberResponse {

    private String email;

    private String name;

    private String phone;

    private String profile;

    private boolean oauth;

    private LocalDateTime lastVisit;
}
