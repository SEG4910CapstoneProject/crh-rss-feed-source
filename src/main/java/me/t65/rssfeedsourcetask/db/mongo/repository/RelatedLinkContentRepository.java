package me.t65.rssfeedsourcetask.db.mongo.repository;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import me.t65.rssfeedsourcetask.db.mongo.RelatedLinkContentEntity;

@Repository
public interface RelatedLinkContentRepository extends MongoRepository<RelatedLinkContentEntity,UUID> {

}
