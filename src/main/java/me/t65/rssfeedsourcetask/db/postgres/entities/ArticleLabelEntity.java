package me.t65.rssfeedsourcetask.db.postgres.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.EqualsAndHashCode
@lombok.Getter
@lombok.Setter
@Entity

@Table(name = "article_labels")
public class ArticleLabelEntity {
    @Id
    @Column(name = "article_id")
    private UUID article_id;

    @Id
    @Column(name = "label_id")
    private int label_id;
}
