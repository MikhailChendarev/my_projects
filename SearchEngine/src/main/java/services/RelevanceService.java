package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class RelevanceService {
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;

    private final Map<String, Lemma> lemmaCache = new ConcurrentHashMap<>();

    public float calculateRelevanceForPage(Map<String, Integer> lemmas) {
        float relevance = 0;
        Set<Lemma> lemmaEntities = new HashSet<>(lemmaRepository.findByLemmaIn(lemmas.keySet()));
        lemmaEntities.forEach(lemmaEntity -> lemmaCache.put(lemmaEntity.getLemma(), lemmaEntity));
        List<Index> indices = indexRepository.findAllByLemmas(lemmaEntities);
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
            String lemma = entry.getKey();
            int frequency = entry.getValue();
            Lemma lemmaEntity = lemmaCache.get(lemma);

            if (lemmaEntity != null) {
                relevance += calculateRelevanceForIndices(indices, lemmaEntity, frequency);
            }
        }
        return relevance;
    }

    private float calculateRelevanceForIndices(List<Index> indices, Lemma lemmaEntity, int frequency) {
        float relevance = 0;
        for (Index index : indices.parallelStream().filter(index -> index.getLemma().equals(lemmaEntity)).toList()) {
            relevance += index.getRating() * frequency;
        }
        return relevance;
    }

    public float findMaxRelevance(Map<Page, Float> relevanceMap) {
        return relevanceMap.values().parallelStream().max(Float::compare).orElse(0f);
    }
}


