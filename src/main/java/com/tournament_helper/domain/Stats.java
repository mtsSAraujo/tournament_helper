package com.tournament_helper.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class Stats {
    @JsonProperty("Kills")
    private int kills;
    @JsonProperty("Deaths")
    private int deaths;
    @JsonProperty("Assists")
    private int assists;
    @JsonProperty("HeroEffectiveDamageDone")
    private float damageDone;
    @JsonProperty("HeroEffectiveDamageTaken")
    private float damageTaken;
    @JsonProperty("HealingGiven")
    private float healingGiven;

    @JsonSetter("HeroEffectiveDamageDone")
    public void setDamageDone(float damageDone) {
        this.damageDone = Math.round(damageDone);
    }

    @JsonSetter("HeroEffectiveDamageTaken")
    public void setDamageTaken(float damageTaken) {
        this.damageTaken = Math.round(damageTaken);
    }

    @JsonSetter("HealingGiven")
    public void setHealingGiven(float healingGiven) {
        this.healingGiven = Math.round(healingGiven);
    }

}
