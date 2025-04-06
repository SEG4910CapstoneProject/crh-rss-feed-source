package me.t65.rssfeedsourcetask.feed;

import me.t65.rssfeedsourcetask.db.postgres.dtos.ArticleDataMain;
import me.t65.rssfeedsourcetask.dto.ArticlePrimary;
import reactor.core.publisher.Flux;

/** Service that reads some data feed */
public interface FeedService {
    /**
     * Get feed entries as a flux
     *
     * @param feedUrlPair feed and source object
     * @return Flux containing stream of entries from feed
     */
    //Flux<ArticleData> getFeed(FeedUrlPair feedUrlPair);


    /**
     * Get processed articles as a flux
     *
     * @param s empty parameter to start the flux
     * @return Flux containing stream of articles processed after being pulled from open cti
     */
    Flux<ArticleDataMain> fetchArticlesFromOpenCti(String s);

    /**
     * Print a single article to the console for debugging purposes
     *
     * @param article article to be printed
     * @return void
     */
    void  printArticles(ArticlePrimary article);
}
