package me.t65.rssfeedsourcetask.db.postgres.composite_keys;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.EqualsAndHashCode

@Embeddable
public class ArticleRelatedLinkId implements Serializable {
    private UUID articleId;
    private int relLinkId;

}
