package me.t65.rssfeedsourcetask.db.postgres.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "article_labels")
public class ArticleLabelEntity {
    @Id
    @Column(name = "article_id")
    private UUID article_id;

    @Id
    @Column(name = "label_id")
    private int label_id;
}


// @Table(name = "related_links")
// public class RelatedLinkEntity {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "rel_link_id", columnDefinition = "SERIAL")
//     private int relLinkId;

//     @Column(name = "related_hash_link")
//     private long relatedHashLink;

//     @Column(name = "source_ID")
//     private int sourceId;

// }
