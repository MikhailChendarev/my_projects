package searchengine.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import searchengine.dto.ErrorResponseDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IndexingInProgressException.class)
    public ResponseEntity<ErrorResponseDto> handleIndexingInProgress(IndexingInProgressException e) {
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IndexingIsNotProgressException.class)
    public ResponseEntity<ErrorResponseDto> handleIndexingIsNotProgress(IndexingIsNotProgressException e) {
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyQueryException.class)
    public ResponseEntity<ErrorResponseDto> handleEmptyQueryException(EmptyQueryException e) {
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SiteNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleSiteNotFoundException(SiteNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponseDto(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}

