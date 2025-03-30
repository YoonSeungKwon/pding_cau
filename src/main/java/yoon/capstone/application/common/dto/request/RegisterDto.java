package yoon.capstone.application.common.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import yoon.capstone.application.common.exception.sequence.ValidationGroup;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {

    @Email(message = "MEMBER_EMAIL_FORMAT", groups = ValidationGroup.EmailFormat.class)
    @NotBlank(message = "MEMBER_EMAIL_BLANK", groups = ValidationGroup.EmailBlank.class)
    private String email;

    @NotBlank(message = "MEMBER_PASSWORD_BLANK", groups = ValidationGroup.PasswordBlank.class)
    @Length(min = 8, groups = ValidationGroup.NameBlank.class)
    private String password;

    @NotBlank(message = "MEMBER_USERNAME_BLANK", groups = ValidationGroup.NameBlank.class)
    @Length(min = 2, groups = ValidationGroup.NameBlank.class)
    private String name;

    private String phone;

}
