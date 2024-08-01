package searchengine.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.model.Page;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.jsoup.Connection.*;

@Service
@Data
@RequiredArgsConstructor
public class SiteScannerService {
    private final PageService pageService;
    private final PageRepository pageRepository;
    public volatile boolean stopFlag = false;

    public void scan(SiteModel siteModel, String url) {
        Deque<String> urlsToProcess = new ArrayDeque<>();
        Set<String> processedUrls = new HashSet<>();
        Set<String> existingPages = pageRepository.findBySiteModel(siteModel).stream()
                .map(Page::getPath)
                .collect(Collectors.toSet());
        urlsToProcess.push(url);
        while (!urlsToProcess.isEmpty() && !stopFlag) {
            String currentUrl = urlsToProcess.pop();
            if (!processedUrls.contains(currentUrl)) {
                processedUrls.add(currentUrl);
                try {
                    Response response = Jsoup.connect(currentUrl).execute();
                    String contentType = response.contentType();
                    if (contentType.startsWith("text/") || contentType.endsWith("/xml") || contentType.endsWith("+xml")) {
                        pageService.processPage(siteModel, currentUrl);
                        Document doc = response.parse();
                        Elements links = doc.select("a[href]");
                        for (Element link : links) {
                            String nextUrl = link.attr("href").startsWith("/") ? siteModel.getUrl() + link.attr("href")
                                    : link.attr("href");
                            String path = nextUrl.replace(siteModel.getUrl(), "");
                            if (nextUrl.startsWith(siteModel.getUrl()) && !existingPages.contains(path)) {
                                urlsToProcess.push(nextUrl);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}