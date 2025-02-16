package com.tournament_helper.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Teams {
    // Ordem da sala custom.
    SRG(1),
    OON(2),
    TOV(3),
    VVQ(4),
    BSA(5),
    TUB(6),
    PTC(7),
    RRR(8),
    ROK(9),
    TGZ(10);

    private int teamId;

    public static String getTeamById(int id) {
        for (Teams team : values()) {
            if (team.getTeamId() == id) {
                return team.name();
            }
        }
        return "Desconhecido";
    }
}
