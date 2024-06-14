package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.dto.SearchResultRs;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.SiteRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final TextProcessorService textProcessorService;

    public List<SearchResultRs> performSearch(String query, String site, int offset, int limit) {
        Map<String, Integer> lemmas = textProcessorService.getLemmas(query);
        List<String> sortedLemmas = lemmas.keySet().stream().toList();
        if (sortedLemmas.isEmpty()) {
            return new ArrayList<>();
        }
        List<Page> pages;
        if (site == null) {
            pages = getPagesByLemmas(sortedLemmas);
        } else {
            SiteModel siteModel = siteRepository.findByUrl(site);
            if (siteModel != null) {
                pages = getPagesByLemmasAndSite(sortedLemmas, siteModel);
            } else {
                throw new IllegalArgumentException("Сайт не найден: " + site);
            }
        }
        if (pages.isEmpty()) {
            return new ArrayList<>();
        }
        List<SearchResultRs> results = calculateRelevance(pages, lemmas);
        results.sort(Comparator.comparing(SearchResultRs::getRelevance).reversed());
        int toIndex = Math.min(results.size(), offset + limit);
        return results.subList(Math.min(offset, results.size()), toIndex);
    }

    private List<Page> getPagesByLemmas(List<String> lemmas) {
        return lemmas.stream()
                .filter(lemma -> lemmaRepository.findByLemma(lemma) != null)
                .flatMap(lemma -> indexRepository.findPagesByLemma(lemma).stream())
                .collect(Collectors.toList());
    }

    private List<Page> getPagesByLemmasAndSite(List<String> lemmas, SiteModel siteModel) {
        return lemmas.stream()
                .map(lemma -> lemmaRepository.findByLemma(lemma))
                .flatMap(lemma -> indexRepository.findPagesByLemmaAndSiteModel(lemma, siteModel).stream())
                .collect(Collectors.toList());
    }

    private List<SearchResultRs> calculateRelevance(List<Page> pages, Map<String, Integer> lemmas) {
        float maxRelevance = 0;
        Map<Page, Float> relevanceMap = new HashMap<>();
        for (Page page : pages) {
            float relevance = 0;
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String lemma = entry.getKey();
                int frequency = entry.getValue();
                Lemma lemmaEntity = lemmaRepository.findByLemma(lemma);
                if (lemmaEntity != null) {
                    List<Index> indices = indexRepository.findByPageAndLemma(page, lemmaEntity);
                    for (Index index : indices) {
                        relevance += index.getRating() * frequency;
                    }
                }
            }
            maxRelevance = Math.max(maxRelevance, relevance);
            relevanceMap.put(page, relevance);
        }
        List<SearchResultRs> searchResults = new ArrayList<>();
        for (Map.Entry<Page, Float> entry : relevanceMap.entrySet()) {
            SearchResultRs searchResultRs = new SearchResultRs();
            searchResultRs.setUri(entry.getKey().getPath());
            searchResultRs.setTitle(extractTitleFromContent(entry.getKey().getContent()));
            searchResultRs.setSnippet(createSnippet(entry.getKey().getContent(), lemmas.keySet()));
            searchResultRs.setRelevance(entry.getValue() / maxRelevance);
            searchResults.add(searchResultRs);
        }
        return searchResults;
    }

    private String extractTitleFromContent(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        return doc.title();
    }

    private String createSnippet(String htmlContent, Set<String> searchTerms) {
        Document doc = Jsoup.parse(htmlContent);
        String text = doc.body().text();
        String[] words = text.split("\\s+");
        StringBuilder snippetBuilder = new StringBuilder();
        int snippetLength = 0;
        for (String word : words) {
            if (searchTerms.contains(word.toLowerCase())) {
                snippetBuilder.append("<b>").append(word).append("</b>").append(" ");
            } else {
                snippetBuilder.append(word).append(" ");
            }
            snippetLength += word.length() + 1;
            if (snippetLength > 150) break;
        }
        return snippetBuilder.toString().trim();
    }
}
