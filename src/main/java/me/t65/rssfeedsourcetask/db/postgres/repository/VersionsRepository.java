package me.t65.rssfeedsourcetask.db.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.t65.rssfeedsourcetask.db.postgres.entities.VersionsEntity;

@Repository
public interface VersionsRepository extends JpaRepository<VersionsEntity, Integer> {
    VersionsEntity findFirstByOrderByVersionIdDesc();    
}
