package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
@Data
public class SiteService {
    private final SitesList sitesList;
    private final IndexRepository indexRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final PageService pageService;
    private final SiteScannerService siteScannerService;
    private ForkJoinPool forkJoinPool = new ForkJoinPool();
    private String[] statuses = { "INDEXED", "FAILED", "INDEXING" };
    private String error = "Ошибка индексации: главная страница сайта не доступна";
    private String userStopIndexing = "Индексация остановлена пользователем";

    @PostConstruct
    public void init() {
        for (Site site : sitesList.getSites()) {
            SiteModel siteModel = mapToEntity(site);
            if (siteRepository.findByUrl(siteModel.getUrl()) == null) {
                siteRepository.save(siteModel);
            }
        }
    }

    public void stopIndexing() {
        stopTasks();
        siteScannerService.stopFlag = true;
        updateSiteModels();
    }

    private void stopTasks() {
        List<Runnable> awaitingTasks = forkJoinPool.shutdownNow();
        for (Runnable task : awaitingTasks) {
            ((Future<?>) task).cancel(true);
        }
    }

    private void updateSiteModels() {
        List<SiteModel> siteModels = siteRepository.findAll();
        for (SiteModel siteModel : siteModels) {
            if (siteModel.getStatus().equals(Status.INDEXING)) {
                siteModel.setStatus(Status.FAILED);
                siteModel.setLastError(userStopIndexing);
            } else if (siteModel.getStatus().equals(Status.INDEXED)) {
                continue;
            }
            siteModel.setStatusTime(Instant.now());
            siteRepository.save(siteModel);
        }
    }

    public boolean isIndexingInProgress() {
        return forkJoinPool.getActiveThreadCount() > 0;
    }

    public void startIndexing() {
        if (forkJoinPool.isShutdown()) {
            forkJoinPool = new ForkJoinPool();
        }
        siteScannerService.stopFlag = false;
        indexRepository.deleteAll();
        lemmaRepository.deleteAll();
        pageRepository.deleteAll();
        siteRepository.deleteAll();
        scanSites();
    }

    private void scanSites() {
        for (Site site : sitesList.getSites()) {
            SiteModel siteModel = mapToEntity(site);
            forkJoinPool.submit(() -> scanSite(siteModel));
        }
    }

    private void scanSite(SiteModel siteModel) {
        try {
            updateSiteStatus(siteModel, Status.valueOf(statuses[2]));
            siteScannerService.scan(siteModel, siteModel.getUrl());
            if (siteScannerService.stopFlag) {
                updateSiteStatusWithError(siteModel, Status.FAILED, userStopIndexing);
                return;
            }
            updateSiteStatus(siteModel, Status.valueOf(statuses[0]));
        } catch (Exception e) {
            updateSiteStatusWithError(siteModel, Status.valueOf(statuses[1]), error);
            e.printStackTrace();
        }
    }

    private void updateSiteStatus(SiteModel siteModel, Status status) {
        siteModel.setStatus(status);
        siteModel.setStatusTime(Instant.now());
        siteRepository.save(siteModel);
    }

    private void updateSiteStatusWithError(SiteModel siteModel, Status status, String error) {
        siteModel.setStatus(status);
        siteModel.setLastError(error);
        siteModel.setStatusTime(Instant.now());
        siteRepository.save(siteModel);
    }


    public SiteModel mapToEntity(Site site) {
        SiteModel siteModel = new SiteModel();
        siteModel.setUrl(site.getUrl());
        siteModel.setName(site.getName());
        return siteModel;
    }

    public Optional<SiteModel> indexPage(String url) {
        Optional<String> baseUrlOpt = sitesList.getSites().stream()
                .map(Site::getUrl)
                .filter(url::startsWith)
                .findFirst();
        if (!baseUrlOpt.isPresent()) {
            return Optional.empty();
        }
        String baseUrl = baseUrlOpt.get();
        SiteModel siteModel = siteRepository.findByUrl(baseUrl);
        pageService.processPage(siteModel, url);
        return Optional.of(siteModel);
    }
}

