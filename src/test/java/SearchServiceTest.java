import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import searchengine.dto.SearchDto;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.RelevanceService;
import searchengine.services.SearchService;
import searchengine.services.TextProcessorService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

public class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    @Mock
    private SiteRepository siteRepository;

    @Mock
    private LemmaRepository lemmaRepository;

    @Mock
    private IndexRepository indexRepository;

    @Mock
    private TextProcessorService textProcessorService;

    @Mock
    private RelevanceService relevanceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPerformSearch() {
        Map<String, Integer> lemmas = new HashMap<>();
        lemmas.put("test", 1);
        when(textProcessorService.getLemmas(any(String.class))).thenReturn(lemmas);
        when(siteRepository.findByUrl(any(String.class))).thenReturn(new SiteModel());
        when(indexRepository.findPagesByLemma(any(String.class))).thenReturn(new ArrayList<>());
        when(relevanceService.calculateRelevanceForPage(anyMap())).thenReturn(1.0f);
        when(relevanceService.findMaxRelevance(anyMap())).thenReturn(1.0f);
        SearchDto result = searchService.performSearch("query", "site", 0, 20);
        assertEquals(true, result.getResult());
        assertEquals(0, result.getCount());
        assertTrue(result.getData().isEmpty());
    }
}



