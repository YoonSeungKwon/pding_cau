package yoon.capstone.application.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yoon.capstone.application.common.enums.ExceptionCode;

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
