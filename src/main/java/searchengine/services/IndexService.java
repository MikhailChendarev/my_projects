package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class IndexService {
    private final IndexRepository indexRepository;
    private final LemmaRepository lemmaRepository;

    public void createIndex(Map<String, Integer> lemmas, Page page) {
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
            String lemmaText = entry.getKey();
            Integer frequency = entry.getValue();
            Lemma lemma = lemmaRepository.findByLemma(lemmaText);
            Index index = new Index();
            index.setPage(page);
            index.setLemma(lemma);
            index.setRating(frequency.floatValue());
            indexRepository.save(index);
        }
    }
}
