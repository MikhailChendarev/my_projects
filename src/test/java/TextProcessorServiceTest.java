import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import searchengine.services.TextProcessorService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TextProcessorServiceTest {

    private TextProcessorService textProcessorService;

    @BeforeEach
    public void setUp() throws IOException {
        textProcessorService = new TextProcessorService();
    }

    @Test
    public void testCleanWord() {
        String word = "Привет!  :*";
        String cleanedWord = textProcessorService.cleanWord(word);
        assertEquals("привет", cleanedWord);
        word = "Hello! ?";
        cleanedWord = textProcessorService.cleanWord(word);
        assertEquals("hello", cleanedWord);
    }

    @Test
    public void testGetLemmas() {
        String text = "Привет привет, world!";
        Map<String, Integer> lemmas = textProcessorService.getLemmas(text);
        assertEquals(2, lemmas.get("привет"));
        assertEquals(1, lemmas.get("world"));
    }

    @Test
    public void testGetWordBaseForms() {
        String word = "приветы";
        List<String> baseForms = textProcessorService.getWordBaseForms(word);
        assertEquals(Collections.singletonList("привет"), baseForms);
    }
}




