package me.t65.rssfeedsourcetask;

import static org.mockito.Mockito.*;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.emitter.EmitterService;
import me.t65.rssfeedsourcetask.feed.ArticleData;
import me.t65.rssfeedsourcetask.feed.FeedService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class RssFeedSourceTaskTests {
    private static final String FAKE_SOURCE_LINK = "http://example.com/fake/";
    private static final Date FAKE_UPDATE_TIME =
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
    private static final List<SourcesEntity> FAKE_SOURCE_ENTITY_LIST =
            new ArrayList<>(
                    Collections.singleton(
                            new SourcesEntity(0, "Fake Name", FAKE_SOURCE_LINK, FAKE_UPDATE_TIME)));
    private static final UUID uuid1 = UUID.fromString("35af3c73-41db-49f2-bc15-dbfeac37482c");
    private static final UUID uuid2 = UUID.fromString("e31b9ce3-31be-48be-8eef-cf77220e8f12");

    @Mock SourcesRepository sourcesRepositoryMock;

    @Mock FeedService feedServiceMock;

    @Mock EmitterService emitterServiceMock;

    @MockBean private Scheduler scheduler = Schedulers.immediate();

    @InjectMocks RssFeedSourceTask rssFeedSourceTask;

    @BeforeEach
    public void beforeEach() {
        rssFeedSourceTask =
                new RssFeedSourceTask(
                        sourcesRepositoryMock, feedServiceMock, emitterServiceMock, scheduler);
    }

    @Test
    public void testRun_success() {
        ArticleData entry1 =
                new ArticleData(
                        new ArticlesEntity(
                                uuid1, -1, new Date(10001), new Date(10002), false, false, 1000),
                        new ArticleContentEntity(
                                uuid1,
                                FAKE_SOURCE_LINK,
                                "fakeName",
                                new Date(10005),
                                "fake description 1"));
        ArticleData entry2 =
                new ArticleData(
                        new ArticlesEntity(
                                uuid2, -2, new Date(10002), new Date(10003), false, false, 1000),
                        new ArticleContentEntity(
                                uuid2,
                                FAKE_SOURCE_LINK,
                                "fakename",
                                new Date(10006),
                                "fake description 2"));

        when(sourcesRepositoryMock.findAll()).thenReturn(FAKE_SOURCE_ENTITY_LIST);
        when(feedServiceMock.getFeed(any())).thenReturn(Flux.just(entry1, entry2));
        when(emitterServiceMock.emitData(any())).thenReturn(Mono.just(true));

        rssFeedSourceTask.run(null);

        verify(emitterServiceMock, times(2)).emitData(any());
    }
}
