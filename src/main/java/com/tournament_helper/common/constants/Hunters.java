package com.tournament_helper.common.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum Hunters {
    BRALL("hero:ronin"),
    GHOST("hero:assault"),
    JIN("hero:stalker"),
    JOULE("hero:storm"),
    MYTH("hero:huntress"),
    SAROS("hero:farshot"),
    SHIV("hero:flex"),
    SHRIKE("hero:sniper"),
    BISHOP("hero:rocketjumper"),
    KINGPIN("hero:hookguy"),
    FELIX("hero:firefox"),
    OATH("hero:shieldbot"),
    ELLUNA("hero:reshealer"),
    EVA("hero:succubus"),
    ZEPH("hero:backlinehealer"),
    BEEBO("hero:beebo"),
    CELESTE("hero:freeze"),
    HUDSON("hero:gunner"),
    VOID("hero:void"),
    CRYSTA("hero:burstcaster");

    private static final Map<String, Hunters> MAP = new HashMap<>();

    static {
        for (Hunters hunter : Hunters.values()) {
            MAP.put(hunter.jsonValue, hunter);
        }
    }

    private final String jsonValue;

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static Hunters fromJson(String value) {
        return MAP.getOrDefault(value, null);
    }
}
