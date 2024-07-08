package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RelevanceService {
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    public float calculateRelevanceForPage(Page page, Map<String, Integer> lemmas) {
        float relevance = 0;
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
            String lemma = entry.getKey();
            int frequency = entry.getValue();
            Lemma lemmaEntity = lemmaRepository.findByLemma(lemma);
            if (lemmaEntity != null) {
                relevance += calculateRelevanceForLemma(page, lemmaEntity, frequency);
            }
        }
        return relevance;
    }

    private float calculateRelevanceForLemma(Page page, Lemma lemmaEntity, int frequency) {
        float relevance = 0;
        List<Index> indices = indexRepository.findByPageAndLemma(page, lemmaEntity);
        for (Index index : indices) {
            relevance += index.getRating() * frequency;
        }
        return relevance;
    }

    public float findMaxRelevance(Map<Page, Float> relevanceMap) {
        return relevanceMap.values().stream().max(Float::compare).orElse(0f);
    }
}
