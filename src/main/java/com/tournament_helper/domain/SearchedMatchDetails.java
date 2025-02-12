package com.tournament_helper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tournament_helper.common.constants.Hunters;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class SearchedMatchDetails {
    @JsonProperty("match_end")
    private String matchEnd;
    @JsonProperty("team_id")
    private int teamId;
    @JsonProperty("hero_asset_id")
    private Hunters hunter;
    @JsonProperty("placement")
    private int placement;
    private Stats stats;
    private MatchPlayer player;
}
