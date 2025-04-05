package com.tournament_helper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class SearchedPlayer {
    @JsonProperty("unique_display_name")
    private String uniqueDisplayName;
    @JsonProperty("display_name")
    private String displayName;
}
