package yoon.capstone.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectException extends RuntimeException{

    private String message;

}
