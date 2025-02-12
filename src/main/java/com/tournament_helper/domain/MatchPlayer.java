package com.tournament_helper.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MatchPlayer {
    @JsonProperty("unique_display_name")
    private String uniqueDisplayName;
    @JsonProperty("display_name")
    private String displayName;
}
