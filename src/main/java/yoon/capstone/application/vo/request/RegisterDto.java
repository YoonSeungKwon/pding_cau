package yoon.capstone.application.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RegisterDto {

    @Email(message = "MEMBER_EMAIL_FORMAT")
    @NotBlank(message = "MEMBER_EMAIL_BLANK")
    private String email;

    @NotBlank(message = "MEMBER_PASSWORD_BLANK")
    private String password;

    @NotBlank(message = "MEMBER_USERNAME_BLANK")
    private String name;


}
