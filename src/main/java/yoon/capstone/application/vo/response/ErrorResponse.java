package yoon.capstone.application.vo.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {

    private HttpStatus status;

    private String code;

    private String message;

    public ErrorResponse(){
        this.status = HttpStatus.BAD_REQUEST;
        this.code = null;
        this.message = null;
    }

}
