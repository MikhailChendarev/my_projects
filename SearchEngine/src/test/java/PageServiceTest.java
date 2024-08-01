import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Connection.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.model.Page;
import searchengine.model.SiteModel;
import searchengine.repositories.IndexRepository;
import searchengine.repositories.PageRepository;
import searchengine.repositories.SiteRepository;
import searchengine.services.IndexService;
import searchengine.services.LemmaService;
import searchengine.services.PageService;
import searchengine.services.TextProcessorService;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PageServiceTest {

    @Mock
    private TextProcessorService textProcessorService;
    @Mock
    private IndexService indexService;
    @Mock
    private LemmaService lemmaService;
    @Mock
    private SiteRepository siteRepository;
    @Mock
    private PageRepository pageRepository;
    @Mock
    private IndexRepository indexRepository;
    @Mock
    private Connection connection;
    @Mock
    private Response response;
    @Mock
    private Document document;

    @InjectMocks
    private PageService pageService;

    private String userAgent = "testUserAgent";
    private String referrer = "testReferrer";

    @BeforeEach
    public void setUp() throws IOException, NoSuchFieldException, IllegalAccessException {
        pageService = new PageService(textProcessorService, indexService, lemmaService, siteRepository, pageRepository, indexRepository);
        setPrivateField(pageService, "userAgent", userAgent);
        setPrivateField(pageService, "referrer", referrer);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testProcessPage() throws IOException {
        SiteModel siteModel = new SiteModel();
        siteModel.setUrl("http://test.com");
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            jsoupMock.when(() -> Jsoup.connect(anyString())).thenReturn(connection);
            when(connection.userAgent(anyString())).thenReturn(connection);
            when(connection.referrer(anyString())).thenReturn(connection);
            when(connection.execute()).thenReturn(response);
            when(response.contentType()).thenReturn("text/html");
            when(response.statusCode()).thenReturn(200);
            when(response.parse()).thenReturn(document);
            when(document.body()).thenReturn(new Element("body").text("Hello world!"));
            when(pageRepository.findByPathAndSiteModel(anyString(), any(SiteModel.class))).thenReturn(null);
            when(textProcessorService.getLemmas(anyString())).thenReturn(Collections.singletonMap("hello", 1));
            pageService.processPage(siteModel, "http://test.com");
            verify(pageRepository, times(1)).save(any(Page.class));
            verify(lemmaService, times(1)).saveLemmas(any(Map.class));
            verify(indexService, times(1)).createIndex(any(Map.class), any(Page.class));
            verify(siteRepository, times(1)).save(any(SiteModel.class));
        }
    }
}
