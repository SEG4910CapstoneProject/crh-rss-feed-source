package me.t65.rssfeedsourcetask.db.postgres.entities;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@Entity
@Table(name = "articles")
public class ArticlesEntity {

    @Id
    @Column(name = "article_ID", columnDefinition = "uuid")
    private UUID articleId;

    @Column(name = "source_ID")
    private Integer sourceId;

    @Column(name = "date_ingested")
    private Date dateIngested;

    @Column(name = "date_published")
    private Date datePublished;

    @Column(name = "is_feature_ext")
    private Boolean isFeatureExt;

    @Column(name = "is_ML_ext")
    private Boolean isMLExt;

    @Column(name = "hashlink")
    private long hashlink;
}
