package me.t65.rssfeedsourcetask.db;

import java.time.Instant;

import me.t65.rssfeedsourcetask.db.postgres.dtos.ArticleDataMain;
import me.t65.rssfeedsourcetask.dto.Article;
import reactor.core.publisher.Mono;

public interface DBService {

    /**
     * Saves the article object to the database
     *
     * @param articleData Article to be pushed to data store
     * @return true on success, false otherwise
     */

    boolean save(ArticleDataMain articleData);

    void saveLastUpdateToDatabase(int sourceId);

    String getLastVersionUpdateTime();
    boolean saveVersion(Instant utcDate);

    /**
     * Takes an object and transforms it into a new one ready for database persistence
     *
     * @param arc article to be trasformed
     * @return a mono containing the newest object
     */

    Mono<ArticleDataMain> transformIntoDbObjects(Article arc);
}
