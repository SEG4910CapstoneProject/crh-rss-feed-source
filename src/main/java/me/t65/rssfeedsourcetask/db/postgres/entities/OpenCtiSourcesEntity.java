package me.t65.rssfeedsourcetask.db.postgres.entities;

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



@Table(name = "open_cti_sources")
public class OpenCtiSourcesEntity {
    @Id
    @Column(name = "source_ID")
    private int sourceId;

    @Column(name = "source_name")
    private String sourceName;

}
