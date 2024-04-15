package yoon.capstone.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import yoon.capstone.application.enums.ErrorCode;

@Getter
@AllArgsConstructor
public class ProjectException extends RuntimeException{

    private String message;
    private HttpStatus status;

    public ProjectException(ErrorCode errorCode){
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus();
    }

}
