package me.t65.rssfeedsourcetask.feed;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;

@lombok.AllArgsConstructor
@lombok.EqualsAndHashCode
@lombok.Getter
@lombok.ToString
public class ArticleData {
    private ArticlesEntity articlesEntity;
    private ArticleContentEntity articleContentEntity;
}
