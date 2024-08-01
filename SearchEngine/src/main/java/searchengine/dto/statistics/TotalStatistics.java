package searchengine.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalStatistics {
    private Integer sites;
    private Integer pages;
    private Integer lemmas;
    private Boolean indexing;
}
