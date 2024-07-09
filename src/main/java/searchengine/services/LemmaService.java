package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.Lemma;
import searchengine.repositories.LemmaRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LemmaService {
    private final LemmaRepository lemmaRepository;
    private final Object lock = new Object();

    public void saveLemmas(Map<String, Integer> lemmas) {
        synchronized (lock) {
            Set<Lemma> lemmaSet = new HashSet<>();
            for (Map.Entry<String, Integer> entry : lemmas.entrySet()) {
                String lemmaText = entry.getKey();
                Lemma lemma = lemmaRepository.findByLemma(lemmaText);
                if (lemma == null) {
                    lemma = new Lemma();
                    lemma.setLemma(lemmaText);
                    lemma.setFrequency(entry.getValue());
                } else {
                    lemma.setFrequency(lemma.getFrequency() + entry.getValue());
                }
                lemmaSet.add(lemma);
            }
            lemmaRepository.saveAll(lemmaSet);
        }
    }
}
