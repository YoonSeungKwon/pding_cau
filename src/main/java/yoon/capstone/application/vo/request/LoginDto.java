package yoon.capstone.application.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import yoon.capstone.application.exception.sequence.ValidationGroup;

@Getter
public class LoginDto {

    @Email(message = "MEMBER_EMAIL_FORMAT", groups = ValidationGroup.EmailFormat.class)
    @NotBlank(message = "MEMBER_EMAIL_BLANK", groups = ValidationGroup.EmailBlank.class)
    private String email;

    @NotBlank(message = "MEMBER_PASSWORD_BLANK", groups = ValidationGroup.PasswordBlank.class)
    private String password;

}
