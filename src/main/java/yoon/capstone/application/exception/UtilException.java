package yoon.capstone.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UtilException extends RuntimeException{

    private String message;

}
