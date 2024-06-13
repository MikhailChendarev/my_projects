package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.SearchResultRs;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.exceptions.IndexingInProgressException;
import searchengine.exceptions.IndexingIsNotProgressException;
import searchengine.services.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {
    private final StatisticsService statisticsService;
    private final SiteService siteService;
    private final SearchService searchService;

    @GetMapping("/statistics")
    public StatisticsResponse statistics() {
        return statisticsService.getStatistics();
    }

    @GetMapping("/startIndexing")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Boolean> startIndexing() {
        if (siteService.isIndexingInProgress()) {
            throw new IndexingInProgressException("Индексация уже запущена");
        }
        siteService.startIndexing();
        return Collections.singletonMap("result", true);
    }

    @PostMapping("/indexPage")
    public Map<String, Boolean> indexPage(@RequestParam String url) {
        siteService.indexPage(url);
        return Collections.singletonMap("result", true);
    }

    @GetMapping("/stopIndexing")
    public Map<String, Boolean> stopIndexing() {
        if (!siteService.isIndexingInProgress()) {
            throw new IndexingIsNotProgressException("Индексация не запущена");
        }
        siteService.stopIndexing();
        return Collections.singletonMap("result", true);
    }

    @GetMapping("/search")
    public List<SearchResultRs> search(@RequestParam String query,
                                       @RequestParam(required = false) String site,
                                       @RequestParam(required = false, defaultValue = "0") int offset,
                                       @RequestParam(required = false, defaultValue = "20") int limit) {
        return searchService.performSearch(query, site, offset, limit);
    }
}


