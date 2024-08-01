import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.PageService;
import searchengine.services.SiteScannerService;
import searchengine.services.SiteService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SiteServiceTest {

    @Mock
    private SitesList sitesList;
    @Mock
    private IndexRepository indexRepository;
    @Mock
    private LemmaRepository lemmaRepository;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private PageRepository pageRepository;
    @Mock
    private PageService pageService;
    @Mock
    private SiteScannerService siteScannerService;

    @InjectMocks
    private SiteService siteService;

    @BeforeEach
    public void setUp() {
        siteService = new SiteService(sitesList, indexRepository, lemmaRepository, siteRepository,
                pageRepository, pageService, siteScannerService);
    }

    @Test
    public void testInit() {
        Site site = new Site();
        site.setUrl("http://test.com");
        when(sitesList.getSites()).thenReturn(List.of(site));
        when(siteRepository.findByUrl(anyString())).thenReturn(null);

        siteService.init();

        verify(siteRepository, times(1)).save(any(SiteModel.class));
    }

    @Test
    public void testStopIndexing() {
        siteService.stopIndexing();
        assertTrue(siteScannerService.stopFlag);
        verify(siteRepository, times(1)).findAll();
    }


    @Test
    public void testIsIndexingInProgress() {
        ForkJoinPool forkJoinPool = mock(ForkJoinPool.class);
        when(forkJoinPool.getActiveThreadCount()).thenReturn(1);
        siteService.setForkJoinPool(forkJoinPool);
        assertTrue(siteService.isIndexingInProgress());
    }

    @Test
    public void testStartIndexing() {
        siteService.startIndexing();
        verify(indexRepository, times(1)).deleteAllNative();
        verify(lemmaRepository, times(1)).deleteAllNative();
        verify(pageRepository, times(1)).deleteAllNative();
        verify(siteRepository, times(1)).deleteAllNative();
    }

    @Test
    public void testIndexPage() {
        String url = "http://test.com";
        Site site = new Site();
        site.setUrl(url);
        when(sitesList.getSites()).thenReturn(List.of(site));
        when(siteRepository.findByUrl(url)).thenReturn(new SiteModel());

        Optional<SiteModel> result = siteService.indexPage(url);
        assertTrue(result.isPresent());
        verify(pageService, times(1)).processPage(any(SiteModel.class), eq(url));
    }
}




