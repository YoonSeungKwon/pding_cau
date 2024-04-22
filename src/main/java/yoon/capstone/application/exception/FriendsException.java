package yoon.capstone.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yoon.capstone.application.enums.ExceptionCode;

@Getter
@AllArgsConstructor
public class FriendsException extends RuntimeException {

    private String message;

    private HttpStatus status;

    public FriendsException(ExceptionCode exceptionCode){
        this.message = exceptionCode.getMessage();
        this.status = exceptionCode.getStatus();
    }

}
