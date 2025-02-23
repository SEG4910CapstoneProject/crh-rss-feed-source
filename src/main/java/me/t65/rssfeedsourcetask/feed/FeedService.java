package me.t65.rssfeedsourcetask.feed;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** Service that reads some data feed */
public interface FeedService {
    /**
     * Get feed entries as a flux
     *
     * @param feedUrlPair feed and source object
     * @return Flux containing stream of entries from feed
     */
    //Flux<ArticleData> getFeed(FeedUrlPair feedUrlPair);




    Mono<String> fetchArticlesFromOpenCti(String s);
}
