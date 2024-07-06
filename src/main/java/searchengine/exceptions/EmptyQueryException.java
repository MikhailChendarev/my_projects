package searchengine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmptyQueryException extends IllegalArgumentException {

    public EmptyQueryException(String message) {
        super(message);
    }
}