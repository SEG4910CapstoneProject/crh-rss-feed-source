package me.t65.rssfeedsourcetask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class objectLabel {
    @JsonProperty("value")
    private String value;
    public objectLabel(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
