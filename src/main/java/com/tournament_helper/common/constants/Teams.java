package com.tournament_helper.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Teams {
    // Ordem da sala custom.
    T86(1),
    WKT(2),
    ART(3),
    F20(4),
    KST(5),
    CNH(6),
    FA2(7),
    NTI(8),
    CHZ(9),
    KCT(10),
    DEA(11),
    FA1(12);

    private final int teamId;

    public static String getTeamById(int id) {
        for (Teams team : values()) {
            if (team.getTeamId() == id) {
                return team.name();
            }
        }
        return "Desconhecido";
    }
}