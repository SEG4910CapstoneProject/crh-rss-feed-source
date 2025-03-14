package me.t65.rssfeedsourcetask.db.postgres.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Table(name = "labels")
@Getter
@Setter
public class LabelsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "label_id",columnDefinition = "SERIAL")
    private int labelId;

    @Column(name = "label_name")
    private String labelName;



}