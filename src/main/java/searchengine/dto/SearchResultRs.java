package searchengine.dto;

import lombok.Data;

@Data
public class SearchResultRs {
    private String uri;
    private String title;
    private String snippet;
    private Float relevance;
}
