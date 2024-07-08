package yoon.capstone.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import yoon.capstone.application.enums.Role;

@Getter
public class MemberSecurityDto {

    private long memberIdx;

    private String email;

    private String refreshToken;

    private Role role;

    public MemberSecurityDto(long idx, String email, String token, Role role){
        this.memberIdx = idx;
        this.email = email;
        this.refreshToken = token;
        this.role = role;
    }

}
