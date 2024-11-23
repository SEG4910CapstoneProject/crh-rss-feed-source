package me.t65.rssfeedsourcetask;

import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.emitter.EmitterService;
import me.t65.rssfeedsourcetask.feed.FeedService;
import me.t65.rssfeedsourcetask.feed.FeedUrlPair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

/** Class that holds runnable task for fetching and storing rss feed data */
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
            Scheduler scheduler) {
        this.sourcesRepository = sourcesRepository;
        this.feedService = feedService;
        this.emitterService = emitterService;
        this.scheduler = scheduler;
    }

    @Override
    public void run(ApplicationArguments args) {
        Flux.fromIterable(sourcesRepository.findAll())
                .map(
                        sourcesEntity ->
                                new FeedUrlPair(
                                        sourcesEntity.getSourceLink(),
                                        sourcesEntity.getSourceName(),
                                        sourcesEntity.getSourceId(),
                                        sourcesEntity.getLastUpdate()))
                .flatMap(feedService::getFeed)
                .flatMap(this.emitterService::emitData)
                .count()
                .doOnNext(count -> LOGGER.info("Completed Task with {} entries", count))
                .subscribeOn(scheduler)
                .block();
    }
}
