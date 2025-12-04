package me.t65.rssfeedsourcetask.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticlePrimary {
    
    @JsonProperty("standard_id")
    private String standard_id;

    @JsonProperty("externalReferences")
    private ExternalReferences externalRef;

    @JsonProperty("objectLabel")
    private List<objectLabel> labels;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("published")
    private String datePublished;
    public ArticlePrimary(String standard_id,ExternalReferences externalRef,List<objectLabel> labels,String name,String description,String datePublished) {
        this.standard_id = standard_id;
        this.externalRef = externalRef;
        this.labels = labels;
        this.name = name;
        this.description = description;
        this.datePublished = datePublished;
    }

    public String getStandardId() {
        return this.standard_id;
    }

    public ExternalReferences getExternalReferences() {
        return this.externalRef;
    }

    public List<objectLabel> getLabels() {
        return this.labels;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDatePublished() {
        return this.datePublished;
    }


}
