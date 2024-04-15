package yoon.capstone.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import yoon.capstone.application.exception.sequence.ValidationGroup;

@Getter
public class RegisterDto {

    @Schema(description = "가입 이메일", example = "test@test.com")
    @Email(message = "MEMBER_EMAIL_FORMAT", groups = ValidationGroup.EmailFormat.class)
    @NotBlank(message = "MEMBER_EMAIL_BLANK", groups = ValidationGroup.EmailBlank.class)
    private String email;

    @Schema(description = "가입 비밀번호", example = "1234")
    @NotBlank(message = "MEMBER_PASSWORD_BLANK", groups = ValidationGroup.PasswordBlank.class)
    private String password;

    @Schema(description = "가입 이름", example = "홍길동")
    @NotBlank(message = "MEMBER_USERNAME_BLANK", groups = ValidationGroup.NameBlank.class)
    private String name;


}
