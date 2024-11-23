package me.t65.rssfeedsourcetask.db.mongo.repository;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleContentRepository extends MongoRepository<ArticleContentEntity, String> {
    ArticleContentEntity findByLink(String link);
}
