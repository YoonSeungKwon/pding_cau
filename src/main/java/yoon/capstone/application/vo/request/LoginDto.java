package yoon.capstone.application.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginDto {

    @Email(message = "MEMBER_EMAIL_FORMAT")
    @NotBlank(message = "MEMBER_EMAIL_BLANK")
    private String email;

    @NotBlank(message = "MEMBER_PASSWORD_BLANK")
    private String password;

}
