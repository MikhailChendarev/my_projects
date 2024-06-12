package searchengine.dto;

import lombok.Data;

@Data
public class ErrorResponseRs {
    private Boolean result;
    private String error;

    public ErrorResponseRs(String error) {
        this.result = false;
        this.error = error;
    }
}
