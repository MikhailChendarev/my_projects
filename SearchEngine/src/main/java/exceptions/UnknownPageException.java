package searchengine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnknownPageException extends IllegalArgumentException {

    public UnknownPageException(String message) {
        super(message);
    }
}