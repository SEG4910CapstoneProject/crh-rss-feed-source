package me.t65.rssfeedsourcetask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodeArticle {
    @JsonProperty("source_name")
    private String source;

    @JsonProperty("url")
    private String url;
    
    public NodeArticle(String source, String url) {
        this.source = source;
        this.url = url;
    }

    public String getSource() {
        return this.source;
    }

    public String getUrl() {
        return this.url;
    }

    public void setSource(String newSource) {
        this.source = newSource;
    }

}
