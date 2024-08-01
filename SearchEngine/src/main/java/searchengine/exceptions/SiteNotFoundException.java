package searchengine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SiteNotFoundException extends IllegalArgumentException {

    public SiteNotFoundException(String message) {
        super(message);
    }
}