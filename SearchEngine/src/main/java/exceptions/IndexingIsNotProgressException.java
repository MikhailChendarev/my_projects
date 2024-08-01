package searchengine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IndexingIsNotProgressException extends RuntimeException {

    public IndexingIsNotProgressException(String message) {
        super(message);
    }
}