package com.tournament_helper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Player {
    private String platform;
    private String uniqueDisplayName;
    private String displayName;
    private String userId;
    private String source;
}
