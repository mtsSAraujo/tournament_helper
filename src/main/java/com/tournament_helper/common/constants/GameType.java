package com.tournament_helper.common.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum GameType {
    CUSTOM("customgame"),
    ARENA("deathmatch"),
    DUOS("duos");

    private static final Map<String, GameType> MAP = new HashMap<>();

    static {
        for (GameType gameType : GameType.values()) {
            MAP.put(gameType.jsonValue, gameType);
        }
    }

    private final String jsonValue;

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static GameType fromJson(String value) {
        return MAP.getOrDefault(value, null);
    }
}
