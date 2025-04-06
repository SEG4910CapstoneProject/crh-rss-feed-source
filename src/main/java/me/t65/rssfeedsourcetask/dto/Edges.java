package me.t65.rssfeedsourcetask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Edges {
    @JsonProperty("node")
    private NodeArticle node;
    public Edges(NodeArticle node) {
        this.node = node;
    }

    public NodeArticle getNode() {
        return this.node;
    }

}
