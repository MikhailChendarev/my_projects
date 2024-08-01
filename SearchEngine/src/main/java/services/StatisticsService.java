package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    public StatisticsResponse getStatistics() {
        Optional<TotalStatistics> total = calculateTotalStatistics();
        Optional<List<DetailedStatisticsItem>> detailed = calculateDetailedStatistics();
        if (!total.isPresent() || !detailed.isPresent()) {
            return new StatisticsResponse(true, new StatisticsData());
        }
        return createStatisticsResponse(total.get(), detailed.get());
    }

    private Optional<TotalStatistics> calculateTotalStatistics() {
        if (siteRepository.count() == 0) {
            return Optional.empty();
        }
        TotalStatistics total = new TotalStatistics();
        total.setSites((int) siteRepository.count());
        total.setPages((int) pageRepository.count());
        total.setLemmas((int) lemmaRepository.count());
        total.setIndexing(siteRepository.existsByStatus(Status.INDEXING));
        return Optional.of(total);
    }

    private Optional<List<DetailedStatisticsItem>> calculateDetailedStatistics() {
        if (siteRepository.count() == 0) {
            return Optional.empty();
        }
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<SiteModel> siteModels = siteRepository.findAll();
        for (SiteModel siteModel : siteModels) {
            DetailedStatisticsItem item = createDetailedStatisticsItem(siteModel);
            detailed.add(item);
        }
        return Optional.of(detailed);
    }

    private DetailedStatisticsItem createDetailedStatisticsItem(SiteModel siteModel) {
        DetailedStatisticsItem item = new DetailedStatisticsItem();
        item.setName(siteModel.getName());
        item.setUrl(siteModel.getUrl());
        item.setPages(pageRepository.countBySiteModel(siteModel));
        item.setLemmas(indexRepository.countByPageIn(pageRepository.findBySiteModel(siteModel)));
        if (siteModel.getStatus() != null) {
            item.setStatus(siteModel.getStatus().name());
        }

        item.setError(siteModel.getLastError());
        item.setStatusTime(Instant.now().getEpochSecond());
        return item;
    }

    private StatisticsResponse createStatisticsResponse(TotalStatistics total, List<DetailedStatisticsItem> detailed) {
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        StatisticsResponse response = new StatisticsResponse();
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}