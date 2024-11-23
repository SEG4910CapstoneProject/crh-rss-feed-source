package me.t65.rssfeedsourcetask.db.postgres.repository;

import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticlesRepository extends JpaRepository<ArticlesEntity, UUID> {
    @Query(
            "SELECT CASE WHEN COUNT(hash) > 0 THEN true ELSE false END FROM ArticlesEntity hash"
                    + " WHERE hash.hashlink = ?1")
    boolean existsHashlink(long hashedLink);
}
