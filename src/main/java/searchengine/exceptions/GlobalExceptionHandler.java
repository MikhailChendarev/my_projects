package searchengine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import searchengine.dto.ErrorResponseRs;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IndexingInProgressException.class)
    public ResponseEntity<ErrorResponseRs> handleIndexingInProgress(IndexingInProgressException e) {
        return new ResponseEntity<>(new ErrorResponseRs(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IndexingIsNotProgressException.class)
    public ResponseEntity<ErrorResponseRs> handleIndexingIsNotProgress(IndexingIsNotProgressException e) {
        return new ResponseEntity<>(new ErrorResponseRs(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}

