package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import searchengine.model.*;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class PageService {
    private final TextProcessorService textProcessorService;
    private final IndexService indexService;
    private final LemmaService lemmaService;
    @Value("${user.agent}")
    private String userAgent;
    @Value("${referrer}")
    private String referrer;
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final IndexRepository indexRepository;

    public void processPage(SiteModel siteModel, String url) {
        try {
            Connection.Response response = Jsoup.connect(url).userAgent(userAgent).referrer(referrer).execute();
            if (response.statusCode() >= 400) {
                return;
            }
            Document doc = response.parse();
            String path = getPathFromUrl(url, siteModel);
            Page page = pageRepository.findByPathAndSiteModel(path, siteModel);
            if (page != null) {
                deleteUnusedIndices(page);
            } else {
                page = createNewPageModel(path, doc, response, siteModel);
            }
            Map<String, Integer> lemmas = textProcessorService.getLemmas(doc.body().text());
            saveLemmasAndCreateIndex(lemmas, page);
            updateSiteModel(siteModel);
        } catch (Exception e) {
            handleException(e, siteModel);
        }
    }

    private String getPathFromUrl(String url, SiteModel siteModel) {
        String path = url.replace(siteModel.getUrl(), "");
        if (!path.startsWith("/")) {
            path = "/" + path;
            if (path.equals("/")) {
                path = siteModel.getUrl();
            }
        }
        return path;
    }

    private void deleteUnusedIndices(Page page) {
        List<Index> indices = indexRepository.findByPage(page);
        List<Index> indicesToDelete = new ArrayList<>();
        for (Index index : indices) {
            Lemma lemma = index.getLemma();
            if (indexRepository.countByLemma(lemma) == 0) {
                indicesToDelete.add(index);
            }
        }
        indexRepository.deleteAll(indicesToDelete);
    }

    private Page createNewPageModel(String path, Document doc, Connection.Response response, SiteModel siteModel) {
        Page page = new Page();
        page.setPath(path);
        page.setContent(doc.html());
        page.setCode(response.statusCode());
        page.setSiteModel(siteModel);
        pageRepository.save(page);
        return page;
    }

    private void saveLemmasAndCreateIndex(Map<String, Integer> lemmas, Page page) {
        lemmaService.saveLemmas(lemmas);
        indexService.createIndex(lemmas, page);
    }

    private void updateSiteModel(SiteModel siteModel) {
        siteModel.setStatusTime(Instant.now());
        siteRepository.save(siteModel);
    }

    private void handleException(Exception e, SiteModel siteModel) {
        siteModel.setLastError(e.getMessage());
        siteRepository.save(siteModel);
        e.printStackTrace();
    }
}
