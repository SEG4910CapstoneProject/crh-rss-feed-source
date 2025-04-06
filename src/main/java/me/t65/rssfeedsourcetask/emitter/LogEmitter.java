package me.t65.rssfeedsourcetask.emitter;

import me.t65.rssfeedsourcetask.db.postgres.dtos.ArticleDataMain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;

/** Test Emitter service that prints articles to Logger */
public class LogEmitter implements EmitterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogEmitter.class);

    @Override
    public Mono<Boolean> emitData(ArticleDataMain dbObj) {
        LOGGER.info(dbObj.toString());
        return Mono.just(true);
    }
}
