package searchengine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchDto {

    private Boolean result;
    private Integer count;
    private String error;
    private List<SearchData> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchData {
        private String site;
        private String siteName;
        private String uri;
        private String title;
        private String snippet;
        private Float relevance;
    }
}