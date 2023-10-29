package yoon.capstone.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.vo.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> UserNameNotFoundError(UsernameNotFoundException e){
        return null;
    }

    //유효성 검사 에러
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> ValidationError(MethodArgumentNotValidException e){
        ErrorResponse response = new ErrorResponse();
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        if(message == null){                                                    //예외처리
            response.setCode("INTERNAL_SERVER_ERROR");
            response.setMessage("알려지지 않은 에러입니다. 고객센터에 문의해주세요.");
        } else if (message.equals(ErrorCode.MEMBER_EMAIL_BLANK.getCode())) {    //이메일 빈칸
            response.setCode(ErrorCode.MEMBER_EMAIL_BLANK.getCode());
            response.setMessage(ErrorCode.MEMBER_EMAIL_BLANK.getMessage());
        } else if (message.equals(ErrorCode.MEMBER_EMAIL_FORMAT.getCode())) {   //이메일 형식
            response.setCode(ErrorCode.MEMBER_EMAIL_FORMAT.getCode());
            response.setMessage(ErrorCode.MEMBER_EMAIL_FORMAT.getMessage());
        } else if (message.equals(ErrorCode.MEMBER_PASSWORD_BLANK.getCode())) { //비밀번호 빈칸
            response.setCode(ErrorCode.MEMBER_PASSWORD_BLANK.getCode());
            response.setMessage(ErrorCode.MEMBER_PASSWORD_BLANK.getMessage());
        } else if (message.equals(ErrorCode.MEMBER_USERNAME_BLANK.getCode())) { //이름 빈칸
            response.setCode(ErrorCode.MEMBER_USERNAME_BLANK.getCode());
            response.setMessage(ErrorCode.MEMBER_USERNAME_BLANK.getMessage());
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
