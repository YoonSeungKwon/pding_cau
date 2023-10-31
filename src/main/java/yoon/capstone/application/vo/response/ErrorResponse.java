package yoon.capstone.application.vo.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ErrorResponse {

    private HttpStatus code;

    private String status;

    private String message;

    public ErrorResponse(){
        this.code = HttpStatus.BAD_REQUEST;
        this.status = null;
        this.message = null;
    }

}
