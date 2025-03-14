package me.t65.rssfeedsourcetask.db;

import java.time.Instant;

import me.t65.rssfeedsourcetask.db.postgres.dtos.ArticleDataMain;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.dto.Article;
import me.t65.rssfeedsourcetask.feed.ArticleData;
import reactor.core.publisher.Mono;

public interface DBService {
    boolean save(ArticleData articleData);

    void saveLastUpdateToDatabase(int sourceId);

    String getLastVersionUpdateTime();
    boolean saveVersion(Instant utcDate);
     Mono<ArticleDataMain> transformIntoDbObjects(Article arc);
}
