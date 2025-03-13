package me.t65.rssfeedsourcetask.db.postgres.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "articles_relatedLinks")
public class ArticleRelatedLinkEntity {
    @Id
    @Column(name = "article_ID",columnDefinition = "uuid")
    private UUID articleId;

    @Id
    @Column(name = "rel_link_id")
    private int relLinkId;


}
