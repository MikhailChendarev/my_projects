package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.*;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

@Data
@Service
@RequiredArgsConstructor
public class SiteService {
    private final SitesList sitesList;
    private final IndexRepository indexRepository;
    private final LemmaRepository lemmaRepository;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final PageService pageService;
    private final SiteScannerService siteScannerService;
    private ForkJoinPool forkJoinPool = new ForkJoinPool();
    private static final String ERROR = "Ошибка индексации: главная страница сайта не доступна";
    private static final String USER_STOP_INDEXING = "Индексация остановлена пользователем";

    @PostConstruct
    public void init() {
        sitesList.getSites().stream()
                .map(this::mapToEntity)
                .filter(siteModel -> siteRepository.findByUrl(siteModel.getUrl()) == null)
                .forEach(siteRepository::save);
    }

    public void stopIndexing() {
        stopTasks();
        siteScannerService.stopFlag = true;
        updateSiteModels();
    }

    private void stopTasks() {
        forkJoinPool.shutdownNow().stream()
                .map(task -> (Future<?>) task)
                .forEach(task -> task.cancel(true));
    }

    private void updateSiteModels() {
        siteRepository.findAll().stream()
                .filter(siteModel -> siteModel.getStatus().equals(Status.INDEXING) || !siteModel.getStatus().equals(Status.INDEXED))
                .forEach(this::updateSiteStatusAsFailed);
    }

    public boolean isIndexingInProgress() {
        return forkJoinPool.getActiveThreadCount() > 0;
    }

    public void startIndexing() {
        siteScannerService.stopFlag = false;
        indexRepository.deleteAllNative();
        lemmaRepository.deleteAllNative();
        pageRepository.deleteAllNative();
        siteRepository.deleteAllNative();
        scanSites();
    }

    private void scanSites() {
        sitesList.getSites().stream()
                .map(this::mapToEntity)
                .forEach(this::submitSiteForScanning);
    }

    private void submitSiteForScanning(SiteModel siteModel) {
        if (forkJoinPool.isShutdown()) {
            forkJoinPool = new ForkJoinPool();
        }
        forkJoinPool.submit(() -> scanSite(siteModel));
    }

    private void scanSite(SiteModel siteModel) {
        try {
            updateSiteStatus(siteModel, Status.INDEXING);
            siteScannerService.scan(siteModel, siteModel.getUrl());
            if (siteScannerService.stopFlag) {
                updateSiteStatusWithError(siteModel, USER_STOP_INDEXING);
                return;
            }
            updateSiteStatus(siteModel, Status.INDEXED);
        } catch (Exception e) {
            updateSiteStatusWithError(siteModel, ERROR);
            e.printStackTrace();
        } finally {
            if (!siteScannerService.stopFlag) {
                updateSiteStatus(siteModel, Status.INDEXED);
                siteModel.setLastError("-");
            }
        }
    }

    private void updateSiteStatus(SiteModel siteModel, Status status) {
        siteModel.setStatus(status);
        siteModel.setStatusTime(Instant.now());
        siteRepository.save(siteModel);
    }

    private void updateSiteStatusWithError(SiteModel siteModel, String error) {
        siteModel.setStatus(Status.FAILED);
        siteModel.setLastError(error);
        siteModel.setStatusTime(Instant.now());
        siteRepository.save(siteModel);
    }

    private void updateSiteStatusAsFailed(SiteModel siteModel) {
        siteModel.setStatus(Status.FAILED);
        siteModel.setLastError(USER_STOP_INDEXING);
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
        return sitesList.getSites().stream()
                .map(Site::getUrl)
                .filter(url::startsWith)
                .findFirst()
                .flatMap(baseUrl -> {
                    SiteModel siteModel = siteRepository.findByUrl(baseUrl);
                    pageService.processPage(siteModel, url);
                    return Optional.of(siteModel);
                });
    }
}