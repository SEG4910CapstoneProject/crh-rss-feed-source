package me.t65.rssfeedsourcetask.db.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.t65.rssfeedsourcetask.db.postgres.entities.OpenCtiSourcesEntity;


@Repository
public interface OpenCtiRepository extends JpaRepository<OpenCtiSourcesEntity, Integer>{
    OpenCtiSourcesEntity findIdBySourceName(String sourceName);
}
