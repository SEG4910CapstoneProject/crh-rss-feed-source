package me.t65.rssfeedsourcetask.db.postgres.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.Getter
@lombok.Setter
@Entity

@Table(name = "related_links")
public class RelatedLinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rel_link_id", columnDefinition = "SERIAL")
    private int relLinkId;

    @Column(name = "related_hash_link")
    private long relatedHashLink;

    @Column(name = "source_ID")
    private int sourceId;

}
