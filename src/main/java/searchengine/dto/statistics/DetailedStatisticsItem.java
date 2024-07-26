package searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailedStatisticsItem {
    private String url;
    private String name;
    private String status;
    private Long statusTime;
    private String error;
    private Integer pages;
    private Integer lemmas;
}
