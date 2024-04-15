package yoon.capstone.application.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yoon.capstone.application.enums.ErrorCode;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UsernameNotFoundException.class})    //로그인 이메일 에러
    public ResponseEntity<String> UserNameNotFoundError(){
        ErrorCode errorCode = ErrorCode.MEMBER_EMAIL_NOTFOUND;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getStatus());
    }

    @ExceptionHandler({BadCredentialsException.class})      //로그인 비밀번호 에러
    public ResponseEntity<String> BadCredentialError(){
        ErrorCode errorCode = ErrorCode.MEMBER_PASSWORD_INVALID;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getStatus());
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
        ErrorCode errorCode = ErrorCode.FILE_SIZE_EXCEEDED;
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getStatus());
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
        ErrorCode errorCode;
        if (Objects.requireNonNull(message).equals(ErrorCode.MEMBER_EMAIL_BLANK.getMessage())) {    //이메일 빈칸
            errorCode = ErrorCode.MEMBER_EMAIL_BLANK;
        } else if (message.equals(ErrorCode.MEMBER_EMAIL_FORMAT.getMessage())) {   //이메일 형식
            errorCode = ErrorCode.MEMBER_EMAIL_FORMAT;
        } else if (message.equals(ErrorCode.MEMBER_PASSWORD_BLANK.getMessage())) { //비밀번호 빈칸
            errorCode = ErrorCode.MEMBER_PASSWORD_BLANK;
        } else if (message.equals(ErrorCode.MEMBER_USERNAME_BLANK.getMessage())) { //이름 빈칸
            errorCode = ErrorCode.MEMBER_USERNAME_BLANK;
        } else{
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(errorCode.getMessage(), errorCode.getStatus());
    }

}
