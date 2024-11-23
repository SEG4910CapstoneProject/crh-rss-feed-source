package me.t65.rssfeedsourcetask.db;

import me.t65.rssfeedsourcetask.feed.ArticleData;

public interface DBService {
    boolean save(ArticleData articleData);

    void saveLastUpdateToDatabase(int sourceId);
}
