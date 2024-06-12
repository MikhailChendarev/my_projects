package searchengine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IndexingInProgressException extends RuntimeException {

    public IndexingInProgressException(String message) {
        super(message);
    }
}
