import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.LemmaRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.StatisticsService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTest {

    @Mock
    private SiteRepository siteRepository;
    @Mock
    private PageRepository pageRepository;
    @Mock
    private LemmaRepository lemmaRepository;
    @Mock
    private IndexRepository indexRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    @BeforeEach
    public void setUp() {
        statisticsService = new StatisticsService(siteRepository, pageRepository, lemmaRepository, indexRepository);
    }

    @Test
    public void testGetStatistics() {
        when(siteRepository.count()).thenReturn(1L);
        when(pageRepository.count()).thenReturn(10L);
        when(lemmaRepository.count()).thenReturn(100L);
        when(siteRepository.existsByStatus(Status.INDEXING)).thenReturn(true);
        SiteModel siteModel = new SiteModel();
        siteModel.setName("Test Site");
        siteModel.setUrl("http://example.com");
        siteModel.setStatus(Status.INDEXING);
        siteModel.setLastError("No error");
        List<SiteModel> siteModels = new ArrayList<>();
        siteModels.add(siteModel);
        when(siteRepository.findAll()).thenReturn(siteModels);
        when(pageRepository.countBySiteModel(any(SiteModel.class))).thenReturn(10);
        when(indexRepository.countByPageIn(anyList())).thenReturn(100);
        StatisticsResponse response = statisticsService.getStatistics();
        assertTrue(response.getResult());
        verify(siteRepository, times(3)).count();
        verify(pageRepository, times(1)).count();
        verify(lemmaRepository, times(1)).count();
        verify(siteRepository, times(1)).existsByStatus(Status.INDEXING);
        verify(siteRepository, times(1)).findAll();
        verify(pageRepository, times(1)).countBySiteModel(any(SiteModel.class));
        verify(indexRepository, times(1)).countByPageIn(anyList());
    }

    @Test
    public void testGetStatisticsEmpty() {
        when(siteRepository.count()).thenReturn(0L);
        StatisticsResponse response = statisticsService.getStatistics();
        assertTrue(response.getResult());
        verify(siteRepository, times(2)).count();
    }
}


