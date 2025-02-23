package me.t65.rssfeedsourcetask.db;

import java.time.Instant;

import me.t65.rssfeedsourcetask.feed.ArticleData;

public interface DBService {
    boolean save(ArticleData articleData);

    void saveLastUpdateToDatabase(int sourceId);

    String getLastVersionUpdateTime();
    boolean saveVersion(Instant utcDate);
}
