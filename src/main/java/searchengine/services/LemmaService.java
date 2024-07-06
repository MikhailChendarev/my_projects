package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Lemma;
import searchengine.repositories.LemmaRepository;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LemmaService {
    private final LemmaRepository lemmaRepository;
    private final Object lock = new Object();

    public void saveLemmas(Map<String, Integer> lemmas) {
        for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
            synchronized (lock) {
                String lemmaText = entry.getKey();
                Lemma lemma = lemmaRepository.findByLemma(lemmaText);
                if (lemma == null) {
                    lemma = new Lemma();
                    lemma.setLemma(lemmaText);
                    lemma.setFrequency(entry.getValue());
                } else {
                    lemma.setFrequency(lemma.getFrequency() + entry.getValue());
                }
                lemmaRepository.save(lemma);
            }
        }
    }
}