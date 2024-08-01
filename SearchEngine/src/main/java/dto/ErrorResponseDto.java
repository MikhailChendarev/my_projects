package searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private Boolean result;
    private String error;

    public ErrorResponseDto(String error) {
        this.result = false;
        this.error = error;
    }
}