package me.t65.rssfeedsourcetask.db.mongo;

import java.util.List;
import java.util.UUID;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Setter;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.EqualsAndHashCode
@lombok.ToString
@lombok.Getter
@Setter
@Document(collection = "relatedLinkContent")
public class RelatedLinkContentEntity {
    @Id
    private UUID id;

    private List<String> links;
    

}


// public class ArticleContentEntity {
//     @Id private UUID id;
//     private String link;
//     private String name;
//     private Date date;
//     private String description;
// }