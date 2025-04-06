package me.t65.rssfeedsourcetask;

import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.emitter.EmitterService;
import me.t65.rssfeedsourcetask.feed.FeedService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

/** Class that holds runnable task for fetching and storing articles data */
@Component
public class RssFeedSourceTask implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RssFeedSourceTask.class);

    private final SourcesRepository sourcesRepository;
    private final FeedService feedService;
    private final EmitterService emitterService;
    private final Scheduler scheduler;

    @Autowired
    public RssFeedSourceTask(
            SourcesRepository sourcesRepository,
            FeedService feedService,
            EmitterService emitterService,
            Scheduler scheduler
            ) 
        {
        this.sourcesRepository = sourcesRepository;
        this.feedService = feedService;
        this.emitterService = emitterService;
        this.scheduler = scheduler;
    }


    // This starts automatically when spring app starts
    // It makes an http request to the open cti endpoint to retrieve all reports (aka articles)
    @Override
    public void run(ApplicationArguments args) {
        LOGGER.info("Pulling articles from the open cti endpoint\n");
        Mono.just("")
        .flatMapMany(feedService::fetchArticlesFromOpenCti) // ensures inner flux is subscribed to
        .flatMap(this.emitterService::emitData) // saves the data to the database
        .doOnError(error -> {
            LOGGER.error("Critical error: {}", error.getMessage());
            System.exit(1);
        })
        .subscribeOn(scheduler)
        .collectList()
        .block();        
    }
}




