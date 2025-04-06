package me.t65.rssfeedsourcetask.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ExternalReferences {
    @JsonProperty("edges")
    private List<Edges> edges;

    public ExternalReferences(List<Edges> edges) {
        this.edges = edges;
    }

    public List<Edges> getEdges() {
        return this.edges;
    }

}
