package me.t65.rssfeedsourcetask.db;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import me.t65.rssfeedsourcetask.db.mongo.repository.ArticleContentRepository;
import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticlesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.feed.ArticleData;
import me.t65.rssfeedsourcetask.testutils.TestUtils;
import me.t65.rssfeedsourcetask.utils.DateUtilsService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class DBServiceImplTests {

    private static final UUID UUID_1 = UUID.fromString("35af3c73-41db-49f2-bc15-dbfeac37482c");
    private static final int SOURCE_ID = -1;
    private static final Date FAKE_CURRENT_DATE = new Date(10000);

    @Mock ArticlesRepository articlesRepository;

    @Mock ArticleContentRepository articleContentRepository;

    @Mock SourcesRepository sourcesRepository;

    @Mock DateUtilsService dateUtilsService;

    @InjectMocks DBServiceImpl dbService;

    // @Test
    // public void testSave_success() {
    //     ArticleData expectedData =
    //             TestUtils.getFakeDbObj(
    //                     UUID_1,
    //                     "fakeTitle",
    //                     "description1",
    //                     "fakeUrl",
    //                     new Date(1000),
    //                     SOURCE_ID,
    //                     new Date(2000));

    //     dbService.save(expectedData);

    //     verify(articlesRepository).save(eq(expectedData.getArticlesEntity()));
    //     verify(articleContentRepository).save(eq(expectedData.getArticleContentEntity()));
    // }

    // @Test
    // public void testSave_failure_exceptionThrown() {
    //     ArticleData expectedData =
    //             TestUtils.getFakeDbObj(
    //                     UUID_1,
    //                     "fakeTitle",
    //                     "description1",
    //                     "fakeUrl",
    //                     new Date(1000),
    //                     SOURCE_ID,
    //                     new Date(2000));

    //     when(articlesRepository.save(any())).thenThrow(new RuntimeException("Fake Exception"));

    //     assertThrows(
    //             RuntimeException.class,
    //             () -> {
    //                 dbService.save(expectedData);
    //             });
    // }

    @Test
    public void testSaveLastUpdateToDatabase_success() {
        SourcesEntity originalSourceEntity =
                new SourcesEntity(SOURCE_ID, "some source", "source link", new Date(1000));
        SourcesEntity expectedSourceEntity =
                new SourcesEntity(SOURCE_ID, "some source", "source link", FAKE_CURRENT_DATE);

        when(sourcesRepository.findById(eq(SOURCE_ID)))
                .thenReturn(Optional.of(originalSourceEntity));
        when(dateUtilsService.getCurrentDate()).thenReturn(FAKE_CURRENT_DATE);

        dbService.saveLastUpdateToDatabase(SOURCE_ID);

        verify(sourcesRepository).save(expectedSourceEntity);
    }

    @Test
    public void testSaveLastUpdateToDatabase_sourceEntityDoesNotExist_noError() {
        when(sourcesRepository.findById(eq(SOURCE_ID))).thenReturn(Optional.empty());

        dbService.saveLastUpdateToDatabase(SOURCE_ID);

        verify(sourcesRepository, never()).save(any());
    }
}
