package me.t65.rssfeedsourcetask.db.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.t65.rssfeedsourcetask.db.postgres.entities.RelatedLinkEntity;

public interface RelatedLinkRepository extends JpaRepository<RelatedLinkEntity,Integer> {

}
