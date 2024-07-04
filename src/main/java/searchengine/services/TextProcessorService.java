package searchengine.services;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
public class TextProcessorService {
    private LuceneMorphology russianMorphology;
    private LuceneMorphology englishMorphology;

    public TextProcessorService() {
        try {
            russianMorphology = new RussianLuceneMorphology();
            englishMorphology = new EnglishLuceneMorphology();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getLemmas(String text) {
        Map<String, Integer> lemmas = new HashMap<>();
        String[] words = text.split("\\s+");
        for (String word : words) {
            word = word.toLowerCase().replaceAll("[^а-яА-ЯёЁa-zA-Z]", "");
            processWord(word, lemmas);
        }
        return lemmas;
    }

    private void processWord(String word, Map<String, Integer> lemmas) {
        if (!word.isEmpty()) {
            List<String> wordBaseForms = getWordBaseForms(word);
            wordBaseForms.forEach(baseForm -> {
                lemmas.put(baseForm, lemmas.getOrDefault(baseForm, 0) + 1);
            });
        }
    }

    public List<String> getWordBaseForms(String word) {
        if (word.matches("[а-яА-ЯёЁ]+")) {
            return russianMorphology.getNormalForms(word);
        } else if (word.matches("[a-zA-Z]+")) {
            return englishMorphology.getNormalForms(word);
        } else {
            return Collections.emptyList();
        }
    }
}
