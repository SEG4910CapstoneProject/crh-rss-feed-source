package me.t65.rssfeedsourcetask.db.mongo.repository;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleContentRepository extends MongoRepository<ArticleContentEntity, UUID> {
    ArticleContentEntity findByLink(String link);
}
