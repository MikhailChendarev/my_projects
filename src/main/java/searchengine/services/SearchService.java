package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.dto.SearchResultRs;
import searchengine.model.Index;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final TextProcessorService textProcessorService;

    public List<SearchResultRs> performSearch(String query, int offset, int limit) {
        Map<String, Integer> lemmas = textProcessorService.getLemmas(query);
        List<String> sortedLemmas = lemmas.keySet().stream().toList();
        if (sortedLemmas.isEmpty()) {
            return new ArrayList<>();
        }
        List<Page> pages = findPagesByLemma(sortedLemmas.get(0));
        for (int i = 1; i < sortedLemmas.size(); i++) {
            List<Page> lemmaPages = findPagesByLemma(sortedLemmas.get(i));
            pages.retainAll(lemmaPages);
            if (pages.isEmpty()) {
                return new ArrayList<>();
            }
        }
        if (pages.isEmpty()) {
            return new ArrayList<>();
        }
        List<SearchResultRs> results = calculateRelevance(pages, lemmas);
        results.sort(Comparator.comparing(SearchResultRs::getRelevance).reversed());
        int toIndex = Math.min(results.size(), offset + limit);
        return results.subList(offset, toIndex);
    }

    private List<SearchResultRs> calculateRelevance(List<Page> pages, Map<String, Integer> lemmas) {
        float maxRelevance = 0;
        Map<Page, Float> relevanceMap = new HashMap<>();
        for (Page page : pages) {
            float relevance = 0;
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String lemma = entry.getKey();
                int frequency = entry.getValue();
                Index index = indexRepository.findByPageAndLemma(page, lemmaRepository.findByLemma(lemma));
                if (index != null) {
                    relevance += index.getRating() * frequency;
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

    private List<Page> findPagesByLemma(String lemma) {
        return indexRepository.findPagesByLemma(lemma);
    }
}
