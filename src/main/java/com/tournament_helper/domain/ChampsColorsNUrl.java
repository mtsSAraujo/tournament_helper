package com.tournament_helper.domain;

import com.tournament_helper.common.constants.Hunters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class ChampsColorsNUrl {
    private Hunters hunter;
    private String color;
    private String gifUrl;
    private String splashUrl;
}
