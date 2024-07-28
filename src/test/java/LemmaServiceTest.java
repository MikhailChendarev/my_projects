import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import searchengine.Application;
import searchengine.config.SitesList;
import searchengine.model.Lemma;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.services.LemmaService;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class LemmaServiceTest {

    @MockBean
    private SitesList sitesList;

    @MockBean
    private LemmaRepository lemmaRepository;

    @MockBean
    private IndexRepository indexRepository;

    @Autowired
    private LemmaService lemmaService;

    @Test
    public void testSaveLemmas() {
        Map<String, Integer> lemmas = new HashMap<>();
        lemmas.put("lemma1", 1);
        lemmas.put("lemma2", 2);
        when(lemmaRepository.findByLemma("lemma1")).thenReturn(null);
        when(lemmaRepository.findByLemma("lemma2")).thenReturn(new Lemma(2L,"lemma2", 2));
        lemmaService.saveLemmas(lemmas);
        ArgumentCaptor<Set<Lemma>> argument = ArgumentCaptor.forClass(Set.class);
        verify(lemmaRepository).saveAll(argument.capture());
        Set<Lemma> capturedArgument = argument.getValue();
        assertEquals(2, capturedArgument.size());
    }

    @Test
    public void testSaveLemmasMultithreaded() {
        int threadsCount = 10;
        int lemmaCountPerThread = 1000;
        String lemmaText = "lemma";
        ExecutorService executor = Executors.newFixedThreadPool(threadsCount);
        CountDownLatch latch = new CountDownLatch(threadsCount);
        Lemma lemma = new Lemma(1L, lemmaText, 0);
        when(lemmaRepository.findByLemma(lemmaText)).thenReturn(lemma);
        for (int i = 0; i < threadsCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < lemmaCountPerThread; j++) {
                        Map<String, Integer> lemmas = new HashMap<>();
                        lemmas.put(lemmaText, 1);
                        lemmaService.saveLemmas(lemmas);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(lemmaRepository, times(threadsCount * lemmaCountPerThread)).saveAll(any(Set.class));
        assertEquals(threadsCount * lemmaCountPerThread, lemma.getFrequency().intValue());
    }
}
