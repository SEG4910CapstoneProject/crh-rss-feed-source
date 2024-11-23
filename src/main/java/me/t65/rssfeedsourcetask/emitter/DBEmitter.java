package me.t65.rssfeedsourcetask.emitter;

import me.t65.rssfeedsourcetask.config.Config;
import me.t65.rssfeedsourcetask.db.DBService;
import me.t65.rssfeedsourcetask.feed.ArticleData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

@Service
public class DBEmitter implements EmitterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBEmitter.class);
    private DBService dbService;
    private final Scheduler scheduler;

    private final Config config;

    @Autowired
    public DBEmitter(DBService dbService, Config config, Scheduler scheduler) {
        this.dbService = dbService;
        this.config = config;
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Boolean> emitData(ArticleData dbObj) {
        return Mono.just(dbObj)
                .map(dbO -> dbService.save(dbObj))
                .retryWhen(getRetrySpec(dbObj))
                .subscribeOn(scheduler);
    }

    /**
     * Generates retry spec to log status of retries
     *
     * @param dbObj The article data being processed
     * @return Retry Spec that logs before every retry
     */
    private RetryBackoffSpec getRetrySpec(ArticleData dbObj) {
        return RetrySpec.fixedDelay(
                        config.getFeedMaxAttempts(),
                        Duration.ofMillis(config.getFeedRetryBackoffMillis()))
                .doBeforeRetry(
                        retrySignal ->
                                LOGGER.warn(
                                        "Retrying database save operation for article data. Retry:"
                                                + " {} Backoff: {}ms",
                                        retrySignal.totalRetriesInARow(),
                                        config.getFeedRetryBackoffMillis()))
                .onRetryExhaustedThrow(
                        (spec, retrySignal) -> {
                            LOGGER.error(
                                    "Retries exhausted. Failed to save article data to database. {}"
                                            + " retries attempted.",
                                    retrySignal.totalRetriesInARow());
                            return retrySignal.failure();
                        });
    }
}
