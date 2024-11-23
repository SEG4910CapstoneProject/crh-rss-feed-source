package me.t65.rssfeedsourcetask.db.postgres.entities;

import jakarta.persistence.*;

import java.util.Date;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.EqualsAndHashCode
@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "sources")
public class SourcesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "source_ID", columnDefinition = "SERIAL")
    private int sourceId;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "source_link")
    private String sourceLink;

    @Column(name = "last_update")
    private Date lastUpdate;
}
