import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import searchengine.Application;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.services.RelevanceService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Application.class)
public class RelevanceServiceTest {

    @MockBean
    private LemmaRepository lemmaRepository;

    @MockBean
    private IndexRepository indexRepository;

    @MockBean
    private SitesList sitesList;

    private RelevanceService relevanceService;

    @BeforeEach
    public void setUp() {
        Site site1 = new Site("http://testsite1.com", "TestSite1");
        Site site2 = new Site("http://testsite2.com", "TestSite2");
        List<Site> sites = Arrays.asList(site1, site2);
        when(sitesList.getSites()).thenReturn(sites);
        relevanceService = new RelevanceService(lemmaRepository, indexRepository);
    }

    @Test
    public void testCalculateRelevanceForPage() {
        Map<String, Integer> lemmas = new HashMap<>();
        lemmas.put("lemma1", 2);
        lemmas.put("lemma2", 3);
        Lemma lemma1 = new Lemma(1L, "lemma1", 2);
        Lemma lemma2 = new Lemma(2L, "lemma2", 3);
        Index index1 = new Index();
        index1.setId(1L);
        index1.setLemma(lemma1);
        index1.setRating(0.8f);
        Index index2 = new Index();
        index2.setId(2L);
        index2.setLemma(lemma2);
        index2.setRating(0.6f);
        when(lemmaRepository.findByLemmaIn(any())).thenReturn(Set.of(lemma1, lemma2));
        when(indexRepository.findAllByLemmas(any())).thenReturn(Arrays.asList(index1, index2));
        float relevance = relevanceService.calculateRelevanceForPage(lemmas);
        assertEquals(3.4f, relevance);
    }

    @Test
    public void testFindMaxRelevance() {
        Map<Page, Float> relevanceMap = new HashMap<>();
        relevanceMap.put(new Page(), 0.8f);
        relevanceMap.put(new Page(), 0.6f);
        float maxRelevance = relevanceService.findMaxRelevance(relevanceMap);
        assertEquals(0.8f, maxRelevance);
    }
}
