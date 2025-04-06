package me.t65.rssfeedsourcetask.db.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.t65.rssfeedsourcetask.db.postgres.composite_keys.ArticleLabelId;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticleLabelEntity;

public interface ArticleLabelRepository extends JpaRepository<ArticleLabelEntity,ArticleLabelId> {

}
