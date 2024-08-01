import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import searchengine.Application;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.SearchDto;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.SiteModel;
import searchengine.services.SearchService;
import searchengine.services.SiteService;
import searchengine.services.StatisticsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @MockBean
    private SiteService siteService;

    @MockBean
    private SearchService searchService;

    @MockBean
    private SitesList sitesList;

    @Test
    public void testGetSitesList() throws Exception {
        List<Site> sites = new ArrayList<>();
        sites.add(new Site("http://microsoft.com", "Microsoft"));
        sites.add(new Site("http://google.com", "Google"));
        when(sitesList.getSites()).thenReturn(sites);
        mockMvc.perform(get("/api/sites"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'url':'http://microsoft.com', 'name':'Microsoft'}," +
                        " {'url':'http://google.com', 'name':'Google'}]"));
    }

    @Test
    public void testStatistics() throws Exception {
        TotalStatistics total = new TotalStatistics(2, 100, 1000, true);
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        detailed.add(new DetailedStatisticsItem("http://microsoft.com", "Microsoft", "OK",
                System.currentTimeMillis(), null, 50, 500));
        detailed.add(new DetailedStatisticsItem("http://google.com", "Google", "OK", System.currentTimeMillis(),
                null, 50, 500));
        StatisticsData statisticsData = new StatisticsData(total, detailed);
        StatisticsResponse statisticsResponse = new StatisticsResponse(true, statisticsData);
        when(statisticsService.getStatistics()).thenReturn(statisticsResponse);
        mockMvc.perform(get("/api/statistics"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'result': true, 'statistics': {" +
                        "'total': {'sites': 2, 'pages': 100, 'lemmas': 1000, 'indexing': true}," +
                        " 'detailed': [{'url': 'http://microsoft.com', 'name': 'Microsoft', 'status': 'OK', 'pages': 50, 'lemmas': 500}," +
                        " {'url': 'http://google.com', 'name': 'Google', 'status': 'OK', 'pages': 50, 'lemmas': 500}]}}"));
    }

    @Test
    public void testStartIndexing() throws Exception {
        when(siteService.isIndexingInProgress()).thenReturn(false);
        mockMvc.perform(get("/api/startIndexing"))
                .andExpect(status().isAccepted())
                .andExpect(content().json("{'result': true}"));
    }

    @Test
    public void testIndexPage() throws Exception {
        String url = "http://microsoft.com";
        when(siteService.indexPage(url)).thenReturn(Optional.of(new SiteModel()));
        mockMvc.perform(post("/api/indexPage").param("url", url))
                .andExpect(status().isOk())
                .andExpect(content().json("{'result': true}"));
    }

    @Test
    public void testStopIndexing() throws Exception {
        when(siteService.isIndexingInProgress()).thenReturn(true);
        mockMvc.perform(get("/api/stopIndexing"))
                .andExpect(status().isOk())
                .andExpect(content().json("{'result': true}"));
    }

    @Test
    public void testSearch() throws Exception {
        String query = "test";
        List<SearchDto.SearchData> data = new ArrayList<>();
        data.add(new SearchDto.SearchData("http://microsoft.com", "Microsoft",
                "/test", "Test Page", "This is a test page.", 1.0f));
        SearchDto searchDto = new SearchDto(true, 1, null, data);
        when(searchService.performSearch(query, null, 0, 20)).thenReturn(searchDto);
        mockMvc.perform(get("/api/search").param("query", query))
                .andExpect(status().isOk())
                .andExpect(content().json("{'result': true, 'count': 1, 'data': [" +
                        "{'site': 'http://microsoft.com', 'siteName': 'Microsoft', 'uri': '/test'," +
                        " 'title': 'Test Page', 'snippet': 'This is a test page.', 'relevance': 1.0}]}"));
    }
}
