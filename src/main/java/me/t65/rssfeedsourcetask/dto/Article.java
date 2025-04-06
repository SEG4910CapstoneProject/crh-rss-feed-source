package me.t65.rssfeedsourcetask.dto;

import java.util.List;
import java.util.UUID;

public class Article {
    private UUID id;
    private String source;
    private String linkPrimary;
    private List<RelatedLink> relatedLinksList;
    private List<String> labels;
    private String name;
    private String description;
    private String datePublished;

    public Article(UUID id,String source,String linkPrimary,List<RelatedLink> relLinks,
    List<String> labels,String name,
           String description, String datePub) {
            this.id = id;
            this.source = source;
            this.linkPrimary = linkPrimary;
            this.relatedLinksList = relLinks;
            this.labels = labels;
            this.name = name;
            this.description = description;
            this.datePublished = datePub;
    }

    public UUID getId() {
        return this.id;
    }

    public String getSource() {
        return this.source;
    }

    public String getLinkPrimary() {
        return this.linkPrimary;
    }

    public List<RelatedLink>  getRelLinks() {
        return this.relatedLinksList;
    }

    public List<String> getLabels() {
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
