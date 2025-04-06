package me.t65.rssfeedsourcetask.dto;

public class RelatedLink {
    private String source;
    private String relatedLink;

    public RelatedLink(String source, String relatedLink) {
        this.source = source;
        this.relatedLink = relatedLink;
    }

    public String getSource() {
        return this.source;
    }

    public String getRelatedLink() {
        return this.relatedLink;
    }

}
