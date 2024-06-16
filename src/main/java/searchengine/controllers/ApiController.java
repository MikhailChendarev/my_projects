package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.SearchDto;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.exceptions.*;
import searchengine.model.SiteModel;
import searchengine.services.*;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

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
        Optional<SiteModel> siteModelOpt = siteService.indexPage(url);
        if (!siteModelOpt.isPresent()) {
            throw new UnknownPageException("Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
        }
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
    public SearchDto search(@RequestParam String query,
                            @RequestParam(required = false) String site,
                            @RequestParam(required = false, defaultValue = "0") int offset,
                            @RequestParam(required = false, defaultValue = "20") int limit) {
        if (query == null || query.isEmpty()) {
            throw new EmptyQueryException("Задан пустой поисковый запрос");
        }
        if (site != null && siteService.getSiteRepository().findByUrl(site) == null) {
            throw new SiteNotFoundException("Сайт не найден: " + site);
        }
        return searchService.performSearch(query, site, offset, limit);
    }
}


