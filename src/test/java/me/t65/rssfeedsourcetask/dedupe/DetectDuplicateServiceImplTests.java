package me.t65.rssfeedsourcetask.dedupe;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.mongo.repository.ArticleContentRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticlesRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DetectDuplicateServiceImplTests {
    @Mock ArticlesRepository articlesRepository;

    @Mock ArticleContentRepository articleContentRepository;

    @InjectMocks DetectDuplicateServiceImpl detectDuplicateService;

    @Test
    public void testIsDuplicateArticle_isDuplicate_success() {
        ArticleContentEntity mockArticleContentEntity = mock(ArticleContentEntity.class);

        when(articlesRepository.existsHashlink(anyLong())).thenReturn(true);
        when(articleContentRepository.findByLink(anyString())).thenReturn(mockArticleContentEntity);

        boolean actual = detectDuplicateService.isDuplicateArticle("SomeLink");

        assertTrue(actual);
    }

    @Test
    public void testIsDuplicateArticle_notDuplicateHasHash_returnsFalse() {
        when(articlesRepository.existsHashlink(anyLong())).thenReturn(true);
        when(articleContentRepository.findByLink(anyString())).thenReturn(null);

        boolean actual = detectDuplicateService.isDuplicateArticle("SomeLink");

        assertFalse(actual);
    }

    @Test
    public void testIsDuplicateArticle_notDuplicateNoHash_returnsFalse() {
        when(articlesRepository.existsHashlink(anyLong())).thenReturn(false);

        boolean actual = detectDuplicateService.isDuplicateArticle("SomeLink");

        assertFalse(actual);
    }
}
