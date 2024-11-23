package me.t65.rssfeedsourcetask.rss;

import static me.t65.rssfeedsourcetask.testutils.TestUtils.*;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.XmlReader;

import me.t65.rssfeedsourcetask.config.Config;
import me.t65.rssfeedsourcetask.db.DBService;
import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.dedupe.DetectDuplicateService;
import me.t65.rssfeedsourcetask.feed.ArticleData;
import me.t65.rssfeedsourcetask.feed.FeedUrlPair;
import me.t65.rssfeedsourcetask.testutils.TestUtils;
import me.t65.rssfeedsourcetask.utils.DateUtilsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class RssFeedServiceTests {

    private static final Date FAKE_LAST_UPDATE_TIME = new Date(5000);
    private static final Date FAKE_CURRENT_DATE = new Date(8000);
    public static final FeedUrlPair FAKE_FEED_URL_PAIR =
            new FeedUrlPair("http://example.com/fake/", "fake source", -1, FAKE_LAST_UPDATE_TIME);
    private static final UUID uuid1 = UUID.fromString("35af3c73-41db-49f2-bc15-dbfeac37482c");
    private static final UUID uuid2 = UUID.fromString("e31b9ce3-31be-48be-8eef-cf77220e8f12");
    private static final UUID uuid3 = UUID.fromString("bdab8a37-371e-4520-8e47-af66a43edd3e");

    private static final String GET_FEED_DUPLICATE_ARTICLE_TEST_FILE =
            "tests/RssFeedServiceTests/getFeedTest-duplicate-article.xml";
    private static final String GET_FEED_BUILD_DATE_TEST_FILE =
            "tests/RssFeedServiceTests/getFeedTest-build-date.xml";
    private static final String GET_FEED_INVALID_TEST_FILE =
            "tests/RssFeedServiceTests/getFeedTest-invalid.xml";

    @Mock private RestTemplate restTemplateMock;

    @Mock private Config configMock;

    @Mock private RssIdGenerator rssIdGenerator;

    @Mock private DateUtilsService dateUtilsService;

    @Mock SourcesRepository sourcesRepositoryMock;

    @Mock private DetectDuplicateService detectDuplicateServiceMock;

    @Mock private DBService dbServiceMock;

    private Scheduler scheduler = Schedulers.immediate();

    private RssFeedService rssFeedService;

    @BeforeEach
    public void beforeEach() {
        rssFeedService =
                new RssFeedService(
                        restTemplateMock,
                        configMock,
                        rssIdGenerator,
                        dateUtilsService,
                        dbServiceMock,
                        detectDuplicateServiceMock,
                        scheduler);
    }

    @Test
    public void testGetFeed_success() throws IOException {
        // arrange
        SyndFeed syndFeedMock = mock(SyndFeed.class);
        XmlReader xmlReader = mock(XmlReader.class);
        SyndEntry fakeEntry1 = createFakeEntry("title1", "description1", "url1", new Date(10000));
        SyndEntry fakeEntry2 = createFakeEntry("title2", "description2", "url2", new Date(10000));
        SyndEntry fakeEntry3 = createFakeEntry("title3", "description3", "url3", new Date(10000));

        when(restTemplateMock.execute(any(URI.class), any(HttpMethod.class), isNull(), any()))
                .thenReturn(Tuples.of(syndFeedMock, xmlReader, true));
        when(syndFeedMock.getEntries()).thenReturn(List.of(fakeEntry1, fakeEntry2, fakeEntry3));
        when(rssIdGenerator.generateId()).thenReturn(uuid1);
        when(dateUtilsService.getCurrentDate()).thenReturn(FAKE_CURRENT_DATE);

        // actual
        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        // Assert
        StepVerifier.create(actual)
                .expectNextMatches(
                        articleData ->
                                Objects.equals(
                                                articleData.getArticleContentEntity().getName(),
                                                "title1")
                                        && Objects.equals(
                                                articleData
                                                        .getArticleContentEntity()
                                                        .getDescription(),
                                                "description1"))
                .expectNextMatches(
                        articleData ->
                                Objects.equals(
                                                articleData.getArticleContentEntity().getName(),
                                                "title2")
                                        && Objects.equals(
                                                articleData
                                                        .getArticleContentEntity()
                                                        .getDescription(),
                                                "description2"))
                .expectNextMatches(
                        articleData ->
                                Objects.equals(
                                                articleData.getArticleContentEntity().getName(),
                                                "title3")
                                        && Objects.equals(
                                                articleData
                                                        .getArticleContentEntity()
                                                        .getDescription(),
                                                "description3"))
                .verifyComplete();

        verify(xmlReader, times(1)).close();
        verify(restTemplateMock, times(1))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    @Test
    public void testGetFeed_UriInvalid_completeSignal() {
        when(configMock.getFeedMaxAttempts()).thenReturn(1);
        when(configMock.getFeedRetryBackoffMillis()).thenReturn(0);

        Flux<ArticleData> actual =
                rssFeedService.getFeed(
                        new FeedUrlPair(
                                "Bad Uri", FAKE_FEED_URL_PAIR.source(), -1, FAKE_LAST_UPDATE_TIME));

        StepVerifier.create(actual).verifyComplete();
    }

    @Test
    public void testGetFeed_invalidFeed_retriesAndComplete() throws IOException {
        ClientHttpResponse mockClientHttpResponse = mock(ClientHttpResponse.class);

        InputStream mockFeedStream = TestUtils.loadFileAsInputStream(GET_FEED_INVALID_TEST_FILE);
        when(mockClientHttpResponse.getBody()).thenReturn(mockFeedStream);

        mockRestTemplateToRunInternalMethod(
                restTemplateMock, mockClientHttpResponse, any(URI.class), any(HttpMethod.class));

        when(configMock.getFeedMaxAttempts()).thenReturn(3);
        when(configMock.getFeedRetryBackoffMillis()).thenReturn(0);

        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        StepVerifier.create(actual).verifyComplete();
        verify(restTemplateMock, times(4))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    @Test
    public void testGetFeed_failedToReadUriNoSuccess_retriesAndComplete() {
        when(restTemplateMock.execute(any(URI.class), any(HttpMethod.class), isNull(), any()))
                .thenThrow(new RuntimeException("Fake Exception"));
        when(configMock.getFeedMaxAttempts()).thenReturn(3);
        when(configMock.getFeedRetryBackoffMillis()).thenReturn(0);

        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        StepVerifier.create(actual).verifyComplete();
        verify(restTemplateMock, times(4))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    @Test
    public void testGetFeed_failedToReadUriThenSuccess_success() throws IOException {
        SyndEntry fakeEntry1 = createFakeEntry("title1", "description1", "url1", new Date(10000));
        SyndFeed syndFeedMock = mock(SyndFeed.class);
        XmlReader xmlReader = mock(XmlReader.class);

        when(restTemplateMock.execute(any(URI.class), any(HttpMethod.class), isNull(), any()))
                .thenThrow(new RuntimeException("Fake Exception"))
                .thenReturn(Tuples.of(syndFeedMock, xmlReader, true));
        when(syndFeedMock.getEntries()).thenReturn(List.of(fakeEntry1));
        when(configMock.getFeedMaxAttempts()).thenReturn(3);
        when(configMock.getFeedRetryBackoffMillis()).thenReturn(0);
        when(rssIdGenerator.generateId()).thenReturn(uuid1);
        when(dateUtilsService.getCurrentDate()).thenReturn(FAKE_CURRENT_DATE);

        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        StepVerifier.create(actual)
                .expectNextMatches(
                        articleData ->
                                Objects.equals(
                                                articleData.getArticleContentEntity().getName(),
                                                "title1")
                                        && Objects.equals(
                                                articleData
                                                        .getArticleContentEntity()
                                                        .getDescription(),
                                                "description1"))
                .verifyComplete();

        verify(xmlReader, times(1)).close();
        verify(restTemplateMock, times(2))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    @Test
    public void testGetFeed_xmlReaderFailToClose_ignoredAndSuccess() throws IOException {
        SyndEntry fakeEntry1 = createFakeEntry("title1", "description1", "url1", new Date(10000));
        SyndFeed syndFeedMock = mock(SyndFeed.class);
        XmlReader xmlReader = mock(XmlReader.class);

        when(restTemplateMock.execute(any(URI.class), any(HttpMethod.class), isNull(), any()))
                .thenReturn(Tuples.of(syndFeedMock, xmlReader, true));
        when(syndFeedMock.getEntries()).thenReturn(List.of(fakeEntry1));
        doThrow(new IOException("Fake Exception")).when(xmlReader).close();
        when(rssIdGenerator.generateId()).thenReturn(uuid2);
        when(dateUtilsService.getCurrentDate()).thenReturn(FAKE_CURRENT_DATE);

        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        // Assert
        StepVerifier.create(actual)
                .expectNextMatches(
                        articleData ->
                                Objects.equals(
                                                articleData.getArticleContentEntity().getName(),
                                                "title1")
                                        && Objects.equals(
                                                articleData
                                                        .getArticleContentEntity()
                                                        .getDescription(),
                                                "description1"))
                .verifyComplete();

        verify(xmlReader, times(1)).close();

        verify(restTemplateMock, times(1))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    @Test
    public void testGetFeed_errorOnTransform_rejectedAndContinue() throws IOException {
        // arrange
        SyndFeed syndFeedMock = mock(SyndFeed.class);
        XmlReader xmlReader = mock(XmlReader.class);
        SyndEntry fakeEntry1 = createFakeEntry("title1", "description1", "url1", new Date(10000));
        SyndEntry fakeEntry2 = mock(SyndEntry.class);

        when(restTemplateMock.execute(any(URI.class), any(HttpMethod.class), isNull(), any()))
                .thenReturn(Tuples.of(syndFeedMock, xmlReader, true));
        when(syndFeedMock.getEntries()).thenReturn(List.of(fakeEntry1, fakeEntry2));
        lenient().when(fakeEntry2.getTitle()).thenThrow(new RuntimeException("Fake Exception"));
        when(rssIdGenerator.generateId()).thenReturn(uuid3);
        when(dateUtilsService.getCurrentDate()).thenReturn(FAKE_CURRENT_DATE);

        // actual
        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        // assert
        StepVerifier.create(actual)
                .expectNextMatches(
                        articleData ->
                                Objects.equals(
                                                articleData.getArticleContentEntity().getName(),
                                                "title1")
                                        && Objects.equals(
                                                articleData
                                                        .getArticleContentEntity()
                                                        .getDescription(),
                                                "description1"))
                .verifyComplete();
        verify(xmlReader, times(1)).close();
        verify(restTemplateMock, times(1))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    @Test
    public void testGetFeed_allFieldsNull_Continue() throws IOException {
        // arrange
        SyndFeed syndFeedMock = mock(SyndFeed.class);
        XmlReader xmlReader = mock(XmlReader.class);
        SyndEntry fakeEntry1 = createFakeEntry(null, null, null, null);

        when(restTemplateMock.execute(any(URI.class), any(HttpMethod.class), isNull(), any()))
                .thenReturn(Tuples.of(syndFeedMock, xmlReader, false));
        lenient().when(syndFeedMock.getEntries()).thenReturn(List.of(fakeEntry1));
        lenient().when(rssIdGenerator.generateId()).thenReturn(uuid1);
        lenient().when(dateUtilsService.getCurrentDate()).thenReturn(FAKE_CURRENT_DATE);

        // actual
        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        // assert
        StepVerifier.create(actual).expectComplete().verify();
        verify(restTemplateMock, times(1))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    @Test
    public void testGetFeed_articleFilteredByBuildDate() throws IOException {
        ClientHttpResponse mockClientHttpResponse = mock(ClientHttpResponse.class);
        InputStream mockFeedStream = TestUtils.loadFileAsInputStream(GET_FEED_BUILD_DATE_TEST_FILE);
        when(mockClientHttpResponse.getBody()).thenReturn(mockFeedStream);

        mockRestTemplateToRunInternalMethod(
                restTemplateMock, mockClientHttpResponse, any(URI.class), any(HttpMethod.class));

        lenient().when(dateUtilsService.getCurrentDate()).thenReturn(FAKE_CURRENT_DATE);

        // actual
        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        // assert
        StepVerifier.create(actual).verifyComplete();
        verify(restTemplateMock, times(1))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    @Test
    public void testGetFeed_duplicateArticle() throws IOException {
        ClientHttpResponse mockClientHttpResponse = mock(ClientHttpResponse.class);

        when(detectDuplicateServiceMock.isDuplicateArticle("url"))
                .thenReturn(false)
                .thenReturn(true);

        InputStream mockFeedStream =
                TestUtils.loadFileAsInputStream(GET_FEED_DUPLICATE_ARTICLE_TEST_FILE);
        when(mockClientHttpResponse.getBody()).thenReturn(mockFeedStream);

        mockRestTemplateToRunInternalMethod(
                restTemplateMock, mockClientHttpResponse, any(URI.class), any(HttpMethod.class));

        lenient().when(rssIdGenerator.generateId()).thenReturn(uuid1);
        lenient().when(dateUtilsService.getCurrentDate()).thenReturn(FAKE_CURRENT_DATE);

        // actual
        Flux<ArticleData> actual = rssFeedService.getFeed(FAKE_FEED_URL_PAIR);

        // assert
        StepVerifier.create(actual).expectNextCount(1).verifyComplete();

        verify(restTemplateMock, times(1))
                .execute(any(URI.class), any(HttpMethod.class), isNull(), any());
    }

    private static void mockRestTemplateToRunInternalMethod(
            RestTemplate restTemplate,
            ClientHttpResponse clientHttpResponse,
            URI uriMatcher,
            HttpMethod httpMethodMatcher) {

        when(restTemplate.execute(uriMatcher, httpMethodMatcher, isNull(), any()))
                // Runs internal methods
                .thenAnswer(
                        invocationOnMock -> {
                            var responseFunction =
                                    invocationOnMock.getArgument(3, ResponseExtractor.class);
                            return responseFunction.extractData(clientHttpResponse);
                        });
    }
}
