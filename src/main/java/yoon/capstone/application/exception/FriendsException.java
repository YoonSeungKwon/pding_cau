package yoon.capstone.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class FriendsException extends RuntimeException {

    private String message;

}
