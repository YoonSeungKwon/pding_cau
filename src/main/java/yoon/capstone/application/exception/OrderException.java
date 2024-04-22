package yoon.capstone.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class OrderException extends RuntimeException{

    private final String message;

    private final HttpStatus status;

    public OrderException(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }


}
