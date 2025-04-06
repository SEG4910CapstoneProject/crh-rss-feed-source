package me.t65.rssfeedsourcetask.db.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.t65.rssfeedsourcetask.db.postgres.composite_keys.ArticleRelatedLinkId;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticleRelatedLinkEntity;

public interface ArticleRelatedLinkRepository extends JpaRepository<ArticleRelatedLinkEntity,ArticleRelatedLinkId> {

}
