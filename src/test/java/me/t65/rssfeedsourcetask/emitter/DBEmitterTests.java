package me.t65.rssfeedsourcetask.emitter;

import static org.mockito.Mockito.*;

import me.t65.rssfeedsourcetask.config.Config;
import me.t65.rssfeedsourcetask.db.DBService;
import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;
import me.t65.rssfeedsourcetask.feed.ArticleData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class DBEmitterTests {
    @Mock private DBService dbService;
    @Mock private Config configMock;

    private Scheduler scheduler = Schedulers.immediate();

    private DBEmitter dbEmitter;

    private static final String FAKE_SOURCE_LINK = "http://example.com/fake/";
    private static final Date FAKE_UPDATE_TIME =
            Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
    private static final List<SourcesEntity> FAKE_SOURCE_ENTITY_LIST =
            new ArrayList<>(
                    Collections.singleton(
                            new SourcesEntity(0, "Fake Name", FAKE_SOURCE_LINK, FAKE_UPDATE_TIME)));
    private static final UUID uuid1 = UUID.fromString("35af3c73-41db-49f2-bc15-dbfeac37482c");

    private ArticleData entry1 =
            new ArticleData(
                    new ArticlesEntity(
                            uuid1, -1, new Date(10001), new Date(10002), false, false, 1000),
                    new ArticleContentEntity(
                            uuid1,
                            FAKE_SOURCE_LINK,
                            "fakeName",
                            new Date(10005),
                            "fake description 1"));

    @BeforeEach
    public void beforeEach() {
        dbEmitter = new DBEmitter(dbService, configMock, scheduler);
    }

    @Test
    public void testEmitData_success() throws IOException {
        when(dbService.save(any())).thenReturn(true);
        when(configMock.getFeedMaxAttempts()).thenReturn(1);
        when(configMock.getFeedRetryBackoffMillis()).thenReturn(0);

        StepVerifier.create(dbEmitter.emitData(entry1)).expectNext(true).verifyComplete();

        verify(dbService, times(1)).save(any());
    }

    @Test
    public void testEmitData_retry_success() throws IOException {
        when(dbService.save(any())).thenThrow(new RuntimeException()).thenReturn(true);
        when(configMock.getFeedMaxAttempts()).thenReturn(1);
        when(configMock.getFeedRetryBackoffMillis()).thenReturn(0);

        StepVerifier.create(dbEmitter.emitData(entry1)).expectNext(true).verifyComplete();

        verify(dbService, times(2)).save(any());
    }

    @Test
    public void testEmitData_retry_skip() throws IOException {
        when(dbService.save(any())).thenThrow(new RuntimeException());
        when(configMock.getFeedMaxAttempts()).thenReturn(3);
        when(configMock.getFeedRetryBackoffMillis()).thenReturn(0);

        StepVerifier.create(dbEmitter.emitData(entry1)).verifyError();

        verify(dbService, times(4)).save(any());
    }
}
