package yoon.capstone.application.common.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yoon.capstone.application.common.enums.ExceptionCode;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UsernameNotFoundException.class})    //로그인 이메일 에러
    public ResponseEntity<String> UserNameNotFoundError(){
        ExceptionCode exceptionCode = ExceptionCode.MEMBER_EMAIL_NOTFOUND;
        return new ResponseEntity<>(exceptionCode.getMessage(), exceptionCode.getStatus());
    }

    @ExceptionHandler({BadCredentialsException.class})      //로그인 비밀번호 에러
    public ResponseEntity<String> BadCredentialError(){
        ExceptionCode exceptionCode = ExceptionCode.MEMBER_PASSWORD_INVALID;
        return new ResponseEntity<>(exceptionCode.getMessage(), exceptionCode.getStatus());
    }

    @ExceptionHandler({FriendsException.class})
    public ResponseEntity<String> FriendsError(FriendsException e){
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    @ExceptionHandler({ProjectException.class})
    public ResponseEntity<String> ProjectsError(ProjectException e){
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }
    @ExceptionHandler({FileSizeLimitExceededException.class, SizeLimitExceededException.class})
    public ResponseEntity<String> FileSizeError(){
        ExceptionCode exceptionCode = ExceptionCode.FILE_SIZE_EXCEEDED;
        return new ResponseEntity<>(exceptionCode.getMessage(), exceptionCode.getStatus());
    }

    @ExceptionHandler({UtilException.class})
    public ResponseEntity<String> UtilError(UtilException e){
        return new ResponseEntity<>(e.getMessage(), e.getStatus());
    }

    //유효성 검사 에러
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<String> ValidationError(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        ExceptionCode exceptionCode;
        if (Objects.requireNonNull(message).equals(ExceptionCode.MEMBER_EMAIL_BLANK.getMessage())) {    //이메일 빈칸
            exceptionCode = ExceptionCode.MEMBER_EMAIL_BLANK;
        } else if (message.equals(ExceptionCode.MEMBER_EMAIL_FORMAT.getMessage())) {   //이메일 형식
            exceptionCode = ExceptionCode.MEMBER_EMAIL_FORMAT;
        } else if (message.equals(ExceptionCode.MEMBER_PASSWORD_BLANK.getMessage())) { //비밀번호 빈칸
            exceptionCode = ExceptionCode.MEMBER_PASSWORD_BLANK;
        } else if (message.equals(ExceptionCode.MEMBER_PASSWORD_LENGTH.getMessage())) { //비밀번호 빈칸
            exceptionCode = ExceptionCode.MEMBER_PASSWORD_LENGTH;
        } else if (message.equals(ExceptionCode.MEMBER_USERNAME_BLANK.getMessage())) { //이름 빈칸
            exceptionCode = ExceptionCode.MEMBER_USERNAME_BLANK;
        } else if (message.equals(ExceptionCode.MEMBER_USERNAME_LENGTH.getMessage())) { //비밀번호 빈칸
            exceptionCode = ExceptionCode.MEMBER_USERNAME_LENGTH;
        } else{
            exceptionCode = ExceptionCode.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(exceptionCode.getMessage(), exceptionCode.getStatus());
    }

}
