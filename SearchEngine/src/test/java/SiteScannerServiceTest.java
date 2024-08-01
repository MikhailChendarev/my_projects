import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.SiteModel;
import searchengine.repositories.PageRepository;
import searchengine.services.PageService;
import searchengine.services.SiteScannerService;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class SiteScannerServiceTest {

    @Mock
    private PageService pageService;
    @Mock
    private PageRepository pageRepository;
    @Mock
    private Connection connection;
    @Mock
    private Connection.Response response;
    @Mock
    private Document document;
    @InjectMocks
    private SiteScannerService siteScannerService;

    private SiteModel siteModel;
    private String url;

    @BeforeEach
    public void setUp() {
        siteModel = new SiteModel();
        url = "http://test.com";
        siteModel.setUrl(url);
    }

    @Test
    public void testScan() throws Exception {
        when(pageRepository.findBySiteModel(siteModel)).thenReturn(Collections.emptyList());
        when(connection.execute()).thenReturn(response);
        when(response.contentType()).thenReturn("text/html");
        when(response.parse()).thenReturn(document);
        Elements links = mock(Elements.class);
        when(document.select("a[href]")).thenReturn(links);
        Element link = mock(Element.class);
        when(link.attr("href")).thenReturn("/nextPage");
        when(links.iterator()).thenReturn(List.of(link).iterator());
        try (MockedStatic<Jsoup> jsoupMockedStatic = mockStatic(Jsoup.class)) {
            jsoupMockedStatic.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
            siteScannerService.scan(siteModel, url);
            verify(pageService, times(1)).processPage(siteModel, url);
            verify(pageService, times(1)).processPage(siteModel, "http://test.com/nextPage");
        }
    }
}
