import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import searchengine.Application;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.services.IndexService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class IndexServiceTest {

    @MockBean
    private IndexRepository indexRepository;

    @MockBean
    private LemmaRepository lemmaRepository;

    @InjectMocks
    private IndexService indexService;

    @MockBean
    private SitesList sitesList;

    @Before
    public void setUp() {
        Site site1 = new Site("http://testsite1.com", "TestSite1");
        Site site2 = new Site("http://testsite2.com", "TestSite2");
        List<Site> sites = Arrays.asList(site1, site2);
        when(sitesList.getSites()).thenReturn(sites);
        indexService = new IndexService(indexRepository, lemmaRepository);
    }

    @Test
    public void createIndex() {
        Map<String, Integer> lemmas = new HashMap<>();
        lemmas.put("lemma1", 1);
        lemmas.put("lemma2", 2);
        Page page = new Page();
        Lemma lemma1 = new Lemma();
        lemma1.setLemma("lemma1");
        Lemma lemma2 = new Lemma();
        lemma2.setLemma("lemma2");
        when(lemmaRepository.findByLemma("lemma1")).thenReturn(lemma1);
        when(lemmaRepository.findByLemma("lemma2")).thenReturn(lemma2);
        indexService.createIndex(lemmas, page);
        verify(indexRepository, times(2)).save(any(Index.class));
    }
}
