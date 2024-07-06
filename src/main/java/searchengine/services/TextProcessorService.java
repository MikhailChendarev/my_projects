package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
public class TextProcessorService {
    private final LuceneMorphology russianMorphology;
    private final LuceneMorphology englishMorphology;

    public TextProcessorService() {
        try {
            russianMorphology = new RussianLuceneMorphology();
            englishMorphology = new EnglishLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при инициализации морфологии", e);
        }
    }

    public Map<String, Integer> getLemmas(String text) {
        Map<String, Integer> lemmas = new HashMap<>();
        String[] words = text.toLowerCase().split("\\s+");
        for (String word : words) {
            word = word.replaceAll("[^а-яА-ЯёЁa-zA-Z]", "");
            if (!word.isEmpty()) {
                getWordBaseForms(word).forEach(baseForm -> lemmas.merge(baseForm, 1, Integer::sum));
            }
        }
        return lemmas;
    }

    List<String> getWordBaseForms(String word) {
        if (word.matches("[а-яА-ЯёЁ]+")) {
            return russianMorphology.getNormalForms(word);
        } else if (word.matches("[a-zA-Z]+")) {
            return englishMorphology.getNormalForms(word);
        } else {
            return Collections.emptyList();
        }
    }
}