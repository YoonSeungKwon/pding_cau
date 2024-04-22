package yoon.capstone.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yoon.capstone.application.enums.ExceptionCode;

@Getter
@AllArgsConstructor
public class ProjectException extends RuntimeException{

    private String message;
    private HttpStatus status;

    public ProjectException(ExceptionCode exceptionCode){
        this.message = exceptionCode.getMessage();
        this.status = exceptionCode.getStatus();
    }

}
