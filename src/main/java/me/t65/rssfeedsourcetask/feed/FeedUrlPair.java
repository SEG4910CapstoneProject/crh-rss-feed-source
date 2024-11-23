package me.t65.rssfeedsourcetask.feed;

import java.util.Date;

/**
 * Record that holds both a source Identifier and url to the source for ingestion
 *
 * @param url the URL of the source
 * @param source the Source identifier
 */
public record FeedUrlPair(String url, String source, int sourceId, Date lastUpdate) {}
