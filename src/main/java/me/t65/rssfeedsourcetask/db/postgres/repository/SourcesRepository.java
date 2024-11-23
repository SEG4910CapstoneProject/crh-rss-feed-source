package me.t65.rssfeedsourcetask.db.postgres.repository;

import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourcesRepository extends JpaRepository<SourcesEntity, Integer> {}
