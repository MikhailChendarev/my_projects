package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.dto.SearchDto;
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
    private final RelevanceService relevanceService;


    public SearchDto performSearch(String query, String site, int offset, int limit) {
        validateQuery(query);
        Map<String, Integer> lemmas = textProcessorService.getLemmas(query);
        List<String> sortedLemmas = lemmas.keySet().stream().toList();
        if (sortedLemmas.isEmpty()) {
            return SearchDto.builder().result(true).count(0).data(new ArrayList<>()).build();
        }
        List<Page> pages = getPages(sortedLemmas, site);
        if (pages.isEmpty()) {
            return SearchDto.builder().result(true).count(0).data(new ArrayList<>()).build();
        }
        Map<Page, Float> relevanceMap = relevanceService.calculateRelevanceForPages(pages, lemmas);
        float maxRelevance = relevanceService.findMaxRelevance(relevanceMap);
        List<SearchDto.SearchData> results = createSearchResults(relevanceMap, maxRelevance, lemmas);
        results.sort(Comparator.comparing(SearchDto.SearchData::getRelevance).reversed());
        int toIndex = Math.min(results.size(), offset + limit);
        List<SearchDto.SearchData> limitedResults = results.subList(Math.min(offset, results.size()), toIndex);
        return SearchDto.builder().result(true).count(limitedResults.size()).data(limitedResults).build();
    }

    private void validateQuery(String query) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Задан пустой поисковый запрос");
        }
    }

    private List<Page> getPages(List<String> lemmas, String site) {
        if (site == null) {
            return getPagesByLemmas(lemmas);
        } else {
            SiteModel siteModel = siteRepository.findByUrl(site);
            if (siteModel != null) {
                return getPagesByLemmasAndSite(lemmas, siteModel);
            } else {
                throw new IllegalArgumentException("Сайт не найден: " + site);
            }
        }
    }

    private List<Page> getPagesByLemmas(List<String> lemmas) {
        return lemmas.stream()
                .filter(lemma -> lemmaRepository.findByLemma(lemma) != null)
                .flatMap(lemma -> indexRepository.findPagesByLemma(lemma).stream())
                .collect(Collectors.toList());
    }

    private List<Page> getPagesByLemmasAndSite(List<String> lemmas, SiteModel siteModel) {
        return lemmas.stream()
                .map(lemmaRepository::findByLemma)
                .flatMap(lemma -> indexRepository.findPagesByLemmaAndSiteModel(lemma, siteModel).stream())
                .collect(Collectors.toList());
    }

    private List<SearchDto.SearchData> createSearchResults(Map<Page, Float> relevanceMap, float maxRelevance, Map<String, Integer> lemmas) {
        List<SearchDto.SearchData> searchResults = new ArrayList<>();
        for (Map.Entry<Page, Float> entry : relevanceMap.entrySet()) {
            SearchDto.SearchData searchData = SearchDto.SearchData.builder()
                    .site(entry.getKey().getSiteModel().getUrl())
                    .uri(entry.getKey().getPath())
                    .siteName(entry.getKey().getSiteModel().getName())
                    .title(extractTitleFromContent(entry.getKey().getContent()))
                    .snippet(createSnippet(entry.getKey().getContent(), lemmas.keySet()))
                    .relevance(entry.getValue() / maxRelevance)
                    .build();
            searchResults.add(searchData);
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

