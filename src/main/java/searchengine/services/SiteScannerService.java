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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

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
        urlsToProcess.push(url);
        while (!urlsToProcess.isEmpty() && !stopFlag) {
            String currentUrl = urlsToProcess.pop();
            if (!processedUrls.contains(currentUrl)) {
                processedUrls.add(currentUrl);
                pageService.processPage(siteModel, currentUrl);
                try {
                    Document doc = Jsoup.connect(currentUrl).get();
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String nextUrl = link.attr("href").startsWith("/") ? siteModel.getUrl() + link.attr("href")
                                : link.attr("href");
                        if (nextUrl.startsWith(siteModel.getUrl())
                                && !pageRepository.existsByPathAndSiteModel(nextUrl.replace(siteModel.getUrl(), ""), siteModel)) {
                            urlsToProcess.push(nextUrl);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

