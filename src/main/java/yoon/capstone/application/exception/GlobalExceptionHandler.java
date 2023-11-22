package yoon.capstone.application.exception;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yoon.capstone.application.enums.ErrorCode;
import yoon.capstone.application.vo.response.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({UsernameNotFoundException.class})    //로그인 이메일 에러
    public ResponseEntity<ErrorResponse> UserNameNotFoundError(){
        ErrorResponse response = new ErrorResponse();
        response.setStatus(ErrorCode.MEMBER_EMAIL_NOTFOUND.getStatus());
        response.setMessage(ErrorCode.MEMBER_EMAIL_NOTFOUND.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BadCredentialsException.class})      //로그인 비밀번호 에러
    public ResponseEntity<ErrorResponse> BadCredentialError(){
        ErrorResponse response = new ErrorResponse();
        response.setStatus(ErrorCode.MEMBER_PASSWORD_NOTFOUND.getStatus());
        response.setMessage(ErrorCode.MEMBER_PASSWORD_NOTFOUND.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({FriendsException.class})
    public ResponseEntity<ErrorResponse> FriendsError(FriendsException e){
        ErrorResponse response = new ErrorResponse();
        String message = e.getMessage();
        if(message == null){
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
            response.setMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        } else if (message.equals(ErrorCode.ALREADY_FRIENDS.getStatus())) {
            response.setStatus(ErrorCode.ALREADY_FRIENDS.getStatus());
            response.setMessage(ErrorCode.ALREADY_FRIENDS.getMessage());
        } else if (message.equals(ErrorCode.SELF_FRIENDS.getStatus())) {
            response.setStatus(ErrorCode.SELF_FRIENDS.getStatus());
            response.setMessage(ErrorCode.SELF_FRIENDS.getMessage());
        } else if (message.equals(ErrorCode.NOT_FRIENDS.getStatus())) {
            response.setStatus(ErrorCode.NOT_FRIENDS.getStatus());
            response.setMessage(ErrorCode.NOT_FRIENDS.getMessage());
        }

        System.out.println("error: " + e);
        System.out.println("Message: " + message);

        return new ResponseEntity<>(response, response.getCode());
    }

    @ExceptionHandler({ProjectException.class})
    public ResponseEntity<ErrorResponse> ProjectsError(ProjectException e){
        ErrorResponse response = new ErrorResponse();
        String message = e.getMessage();
        if(message == null){
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
            response.setMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        } else if (message.equals(ErrorCode.PROJECT_OWNER.getStatus())) {
            response.setStatus(ErrorCode.PROJECT_OWNER.getStatus());
            response.setMessage(ErrorCode.PROJECT_OWNER.getMessage());
        } else if (message.equals(ErrorCode.TITLE_NOT_BLANK.getStatus())) {
            response.setStatus(ErrorCode.TITLE_NOT_BLANK.getStatus());
            response.setMessage(ErrorCode.TITLE_NOT_BLANK.getMessage());
        } else if (message.equals(ErrorCode.LINK_NOT_BLANK.getStatus())) {
            response.setStatus(ErrorCode.LINK_NOT_BLANK.getStatus());
            response.setMessage(ErrorCode.LINK_NOT_BLANK.getMessage());
        } else if (message.equals(ErrorCode.GOAL_NOT_BLANK.getStatus())) {
            response.setStatus(ErrorCode.GOAL_NOT_BLANK.getStatus());
            response.setMessage(ErrorCode.GOAL_NOT_BLANK.getMessage());
        } else if (message.equals(ErrorCode.DATE_NOT_BLANK.getStatus())) {
            response.setStatus(ErrorCode.DATE_NOT_BLANK.getStatus());
            response.setMessage(ErrorCode.DATE_NOT_BLANK.getMessage());
        }

        System.out.println("error: " + e);
        System.out.println("Message: " + message);

        return new ResponseEntity<>(response, response.getCode());
    }
    @ExceptionHandler({FileSizeLimitExceededException.class, SizeLimitExceededException.class})
    public ResponseEntity<ErrorResponse> FileSizeError(){
        ErrorResponse response = new ErrorResponse();
        response.setStatus(ErrorCode.FILE_SIZE_EXCEEDED.getStatus());
        response.setMessage(ErrorCode.FILE_SIZE_EXCEEDED.getMessage());
        return new ResponseEntity<>(response, response.getCode());
    }

    @ExceptionHandler({UtilException.class})
    public ResponseEntity<ErrorResponse> UtilError(UtilException e){
        ErrorResponse response = new ErrorResponse();
        String message = e.getMessage();
        if(message == null){
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
            response.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
            response.setMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        } else if (message.equals(ErrorCode.NOT_IMAGE_FORMAT.getStatus())) {
            response.setStatus(ErrorCode.NOT_IMAGE_FORMAT.getStatus());
            response.setMessage(ErrorCode.NOT_IMAGE_FORMAT.getMessage());
        }

        System.out.println("error: " + e);
        System.out.println("Message: " + message);

        return new ResponseEntity<>(response, response.getCode());
    }

    //유효성 검사 에러
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> ValidationError(MethodArgumentNotValidException e){
        ErrorResponse response = new ErrorResponse();
        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getAllErrors().get(0).getDefaultMessage();
        if(message == null){
            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);//기타 에러
            response.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
            response.setMessage(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        } else if (message.equals(ErrorCode.MEMBER_EMAIL_BLANK.getStatus())) {    //이메일 빈칸
            response.setStatus(ErrorCode.MEMBER_EMAIL_BLANK.getStatus());
            response.setMessage(ErrorCode.MEMBER_EMAIL_BLANK.getMessage());
        } else if (message.equals(ErrorCode.MEMBER_EMAIL_FORMAT.getStatus())) {   //이메일 형식
            response.setStatus(ErrorCode.MEMBER_EMAIL_FORMAT.getStatus());
            response.setMessage(ErrorCode.MEMBER_EMAIL_FORMAT.getMessage());
        } else if (message.equals(ErrorCode.MEMBER_PASSWORD_BLANK.getStatus())) { //비밀번호 빈칸
            response.setStatus(ErrorCode.MEMBER_PASSWORD_BLANK.getStatus());
            response.setMessage(ErrorCode.MEMBER_PASSWORD_BLANK.getMessage());
        } else if (message.equals(ErrorCode.MEMBER_USERNAME_BLANK.getStatus())) { //이름 빈칸
            response.setStatus(ErrorCode.MEMBER_USERNAME_BLANK.getStatus());
            response.setMessage(ErrorCode.MEMBER_USERNAME_BLANK.getMessage());
        }
        return new ResponseEntity<>(response, response.getCode());
    }

}
