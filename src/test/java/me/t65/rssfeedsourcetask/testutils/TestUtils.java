package me.t65.rssfeedsourcetask.testutils;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.feed.ArticleData;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;

public class TestUtils {

    public static SyndEntry createFakeEntry(
            String title, String description, String url, Date publishDate) {
        SyndEntry result = new SyndEntryImpl();
        SyndContent descriptionContent = new SyndContentImpl();
        descriptionContent.setValue(description);

        result.setTitle(title);
        result.setDescription(descriptionContent);
        result.setUri(url);
        result.setPublishedDate(publishDate);
        result.setLink(url);

        return result;
    }

    public static ArticleData getFakeDbObj(
            UUID uuid,
            String title,
            String description,
            String url,
            Date publishDate,
            int sourceId,
            Date curDate) {
        return transformEntryToDbObj(
                uuid, createFakeEntry(title, description, url, publishDate), sourceId, curDate);
    }

    public static ArticleData transformEntryToDbObj(
            UUID uuid, SyndEntry entry, int sourceId, Date curDate) {
        ArticlesEntity articlesEntity =
                new ArticlesEntity(
                        uuid, sourceId, curDate, entry.getPublishedDate(), false, false, 1000);
        ArticleContentEntity articleContentEntity =
                new ArticleContentEntity(
                        uuid,
                        entry.getLink(),
                        entry.getTitle(),
                        entry.getPublishedDate(),
                        entry.getDescription().getValue());

        return new ArticleData(articlesEntity, articleContentEntity);
    }

    public static InputStream loadFileAsInputStream(String path) {
        return ClassLoader.getSystemResourceAsStream(path);
    }

    public static String loadFileAsString(String path) {
        try {
            return String.join(
                    "", IOUtils.readLines(loadFileAsInputStream(path), Charset.defaultCharset()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
