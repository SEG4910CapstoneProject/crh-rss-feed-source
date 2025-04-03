package me.t65.rssfeedsourcetask.db.postgres.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.t65.rssfeedsourcetask.db.postgres.entities.LabelsEntity;

@Repository
public interface LabelsRepository extends JpaRepository<LabelsEntity,Integer> {
    LabelsEntity findByLabelName(String label);


}
