package com.tournament_helper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tournament_helper.common.constants.GameType;
import com.tournament_helper.common.constants.Hunters;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Match {
    @JsonProperty("match_id")
    private String matchId;
    @JsonProperty("queue_id")
    private GameType typeOfMatch;
    @JsonProperty("match_start")
    private Date matchStart;
    @JsonProperty("match_end")
    private Date matchEnd;
    @JsonProperty("hero_asset_id")
    private Hunters heroAssetId;
    private Stats stats;
}

