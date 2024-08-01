package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.dto.SearchDto;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.SiteRepository;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Function;
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
        Map<String, Integer> lemmas = textProcessorService.getLemmas(query);
        List<String> sortedLemmas = lemmas.keySet().stream().toList();
        List<Page> pages = getPages(sortedLemmas, site);
        if (sortedLemmas.isEmpty() || pages.isEmpty()) {
            return SearchDto.builder().result(true).count(0).data(new ArrayList<>()).build();
        }
        ConcurrentMap<Page, Float> relevanceMap = pages.parallelStream()
                .collect(Collectors.toConcurrentMap(Function.identity(), page -> relevanceService.calculateRelevanceForPage(lemmas)));
        float maxRelevance = relevanceService.findMaxRelevance(relevanceMap);
        List<SearchDto.SearchData> allResults = createSearchResults(relevanceMap, maxRelevance, lemmas);
        allResults.sort(Comparator.comparing(SearchDto.SearchData::getRelevance).reversed());
        List<SearchDto.SearchData> limitedResults = getLimitedResults(allResults, offset, limit);
        return SearchDto.builder()
                .result(true)
                .count(allResults.size())
                .data(limitedResults)
                .build();
    }

    private List<SearchDto.SearchData> getLimitedResults(List<SearchDto.SearchData> allResults, int offset, int limit) {
        int toIndex = Math.min(allResults.size(), offset + limit);
        return allResults.subList(Math.min(offset, allResults.size()), toIndex);
    }

    private List<Page> getPages(List<String> lemmas, String site) {
        if (site == null) {
            return getPagesByLemmas(lemmas);
        } else {
            SiteModel siteModel = siteRepository.findByUrl(site);
            if (siteModel != null) {
                return getPagesByLemmasAndSite(lemmas, siteModel);
            } else {
                return new ArrayList<>();
            }
        }
    }

    private List<Page> getPages(List<String> lemmas, BiFunction<Lemma, SiteModel, List<Page>> findPages, SiteModel siteModel) {
        Map<Page, Long> pageCounts = new HashMap<>();
        for (String lemma : lemmas) {
            Lemma lemmaModel = lemmaRepository.findByLemma(lemma);
            if (lemmaModel != null) {
                List<Page> pagesForLemma = findPages.apply(lemmaModel, siteModel);
                for (Page page : pagesForLemma) {
                    pageCounts.put(page, pageCounts.getOrDefault(page, 0L) + 1);
                }
            } else {
                return new ArrayList<>();
            }
        }
        return pageCounts.entrySet().stream()
                .filter(entry -> entry.getValue() == lemmas.size())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<Page> getPagesByLemmas(List<String> lemmas) {
        return getPages(lemmas, (lemma, siteModel) -> indexRepository.findPagesByLemma(lemma.getLemma()), null);
    }

    private List<Page> getPagesByLemmasAndSite(List<String> lemmas, SiteModel siteModel) {
        return getPages(lemmas, indexRepository::findPagesByLemmaAndSiteModel, siteModel);
    }

    private List<SearchDto.SearchData> createSearchResults(Map<Page, Float> relevanceMap, float maxRelevance, Map<String, Integer> lemmas) {
        List<SearchDto.SearchData> searchResults = new ArrayList<>();
        for (Map.Entry<Page, Float> entry : relevanceMap.entrySet()) {
            SearchDto.SearchData searchData = SearchDto.SearchData.builder()
                    .site(entry.getKey().getSiteModel().getUrl())
                    .uri(entry.getKey().getPath())
                    .siteName(entry.getKey().getSiteModel().getName())
                    .title(highlightTitle(Jsoup.parse(entry.getKey().getContent()).title(), lemmas.keySet()))
                    .snippet(createSnippet(entry.getKey().getContent(), lemmas.keySet()))
                    .relevance(entry.getValue() / maxRelevance)
                    .build();
            searchResults.add(searchData);
        }
        return searchResults;
    }

    private String createSnippet(String htmlContent, Set<String> searchTerms) {
        String[] words = Jsoup.parse(htmlContent).body().text().split("\\s+");
        int totalLength = 0;
        boolean found = false;
        StringBuilder snippetBuilder = new StringBuilder();
        for (String word : words) {
            String cleanWord = textProcessorService.cleanWord(word);
            boolean isMatch = isMatch(cleanWord, searchTerms);
            if (isMatch && !found) {
                appendEllipsisIfNecessary(snippetBuilder, totalLength);
                found = true;
            }
            if (found) {
                appendWordToSnippet(snippetBuilder, word, isMatch);
                if (snippetBuilder.length() > 500) break;
            }
            totalLength += word.length() + 1;
        }
        return snippetBuilder.toString().trim();
    }

    private boolean isMatch(String cleanWord, Set<String> searchTerms) {
        List<String> wordBaseForms = textProcessorService.getWordBaseForms(cleanWord);
        return wordBaseForms.stream().anyMatch(searchTerms::contains);
    }

    private void appendEllipsisIfNecessary(StringBuilder snippetBuilder, int totalLength) {
        if (totalLength > 200) {
            snippetBuilder.append("... ");
        }
    }

    private void appendWordToSnippet(StringBuilder snippetBuilder, String word, boolean isMatch) {
        if (isMatch) {
            snippetBuilder.append("<b>").append(word).append("</b>").append(" ");
        } else {
            snippetBuilder.append(word).append(" ");
        }
    }

    private String highlightTitle(String title, Set<String> searchTerms) {
        String[] words = title.split("\\s+");
        StringBuilder titleBuilder = new StringBuilder();
        for (String word : words) {
            String cleanWord = textProcessorService.cleanWord(word);
            boolean isMatch = isMatch(cleanWord, searchTerms);
            appendWordToSnippet(titleBuilder, word, isMatch);
        }
        return titleBuilder.toString().trim();
    }
}