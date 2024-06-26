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
    private Set<String> stopWords = new HashSet<>(Arrays.asList("и", "но", "а", "что", "как", "это", "так", "вот", "быть", "к", "в", "с", "для"));

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
            processWord(word, lemmas);
        }
        return lemmas;
    }

    private void processWord(String word, Map<String, Integer> lemmas) {
        word = word.toLowerCase(Locale.ROOT).replaceAll("[^а-яА-ЯёЁa-zA-Z]", "");
        if (!word.isEmpty() && !stopWords.contains(word)) {
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




