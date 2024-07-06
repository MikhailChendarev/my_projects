package searchengine.dto;

import lombok.Data;

@Data
public class ErrorResponseDto {
    private Boolean result;
    private String error;

    public ErrorResponseDto(String error) {
        this.result = false;
        this.error = error;
    }
}