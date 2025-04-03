package me.t65.rssfeedsourcetask.db.postgres.entities;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import me.t65.rssfeedsourcetask.db.postgres.composite_keys.ArticleLabelId;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.EqualsAndHashCode
@lombok.Getter
@lombok.Setter
@Entity

@Table(name = "article_labels")
public class ArticleLabelEntity {
    @EmbeddedId
    private ArticleLabelId id;

    // @Id
    // @Column(name = "article_id")
    // private UUID article_id;

    // @Id
    // @Column(name = "label_id")
    // private int label_id;
}
