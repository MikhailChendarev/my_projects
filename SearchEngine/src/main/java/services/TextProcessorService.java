package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TextProcessorService {
    private final LuceneMorphology russianMorphology;
    private final LuceneMorphology englishMorphology;
    private final Map<String, List<String>> baseFormsCache = new ConcurrentHashMap<>();
    private final String WORD_CLEANUP_REGEX = "[^а-яА-ЯёЁa-zA-Z]";

    public String cleanWord(String word) {
        return word.toLowerCase().replaceAll(WORD_CLEANUP_REGEX, "");
    }

    public TextProcessorService() {
        try {
            russianMorphology = new RussianLuceneMorphology();
            englishMorphology = new EnglishLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при инициализации морфологии", e);
        }
    }

    public Map<String, Integer> getLemmas(String text) {
        Map<String, Integer> lemmas = new ConcurrentHashMap<>();
        Arrays.stream(text.toLowerCase().split("\\s+")).parallel().forEach(word -> {
            word = cleanWord(word);
            if (!word.isEmpty()) {
                getWordBaseForms(word).forEach(baseForm -> lemmas.merge(baseForm, 1, Integer::sum));
            }
        });
        return lemmas;
    }

    public List<String> getWordBaseForms(String word) {
        return baseFormsCache.computeIfAbsent(word, this::calculateBaseForms);
    }

    private List<String> calculateBaseForms(String word) {
        if (word.matches("[а-яА-ЯёЁ]+")) {
            return russianMorphology.getNormalForms(word);
        } else if (word.matches("[a-zA-Z]+")) {
            return englishMorphology.getNormalForms(word);
        } else {
            return Collections.emptyList();
        }
    }
}
