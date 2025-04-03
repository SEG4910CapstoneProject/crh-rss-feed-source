package me.t65.rssfeedsourcetask.emitter;

import me.t65.rssfeedsourcetask.db.postgres.dtos.ArticleDataMain;

import reactor.core.publisher.Mono;

/** Service that emits data to some datastore */
public interface EmitterService {
    /**
     * Emit data to data store
     *
     * @param dbObj Data to push to data store
     * @return Mono of boolean on success. True if successful, false otherwise
     */
    Mono<Boolean> emitData(ArticleDataMain dbObj);
}
