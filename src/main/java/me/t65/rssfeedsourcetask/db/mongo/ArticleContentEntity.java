package me.t65.rssfeedsourcetask.db.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.EqualsAndHashCode
@lombok.ToString
@lombok.Getter
@Document(collection = "articleContent")
public class ArticleContentEntity {
    @Id private UUID id;
    private String link;
    private String name;
    private Date date;
    private String description;
}
