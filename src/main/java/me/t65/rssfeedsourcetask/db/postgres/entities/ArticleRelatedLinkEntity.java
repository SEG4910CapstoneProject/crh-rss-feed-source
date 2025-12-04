package me.t65.rssfeedsourcetask.db.postgres.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.t65.rssfeedsourcetask.db.postgres.composite_keys.ArticleRelatedLinkId;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@Entity
@lombok.Getter
@lombok.Setter
@Table(name = "articles_related_links")
public class ArticleRelatedLinkEntity {
    @EmbeddedId
    private ArticleRelatedLinkId id;
}
