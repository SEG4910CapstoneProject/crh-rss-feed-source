package me.t65.rssfeedsourcetask.rss;

import java.util.UUID;

/** Minimal interface from which generators fpr ids for articles are derived */
public interface RssIdGenerator {
    /**
     * Generates UUID For Article
     *
     * @return UUID Object representing article id
     */
    UUID generateId();
}
