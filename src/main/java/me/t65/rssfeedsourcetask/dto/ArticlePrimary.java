package me.t65.rssfeedsourcetask.dto;

import java.util.List;

public class ArticlePrimary {
    
    private String standard_id;
    private ExternalReferences externalRef;
    private List<objectLabel> labels;
    private String name;
    private String description;
    private String datePublished;
    public ArticlePrimary(String standard_id,ExternalReferences externalRef,List<objectLabel> labels,String name,String description,String datePublished) {
        this.standard_id = standard_id;
        this.externalRef = externalRef;
        this.labels = labels;
        this.name = name;
        this.description = description;
        this.datePublished = datePublished;
    }


}
