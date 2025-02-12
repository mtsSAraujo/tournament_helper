package com.tournament_helper.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Teams {
    // Ordem da sala custom.
    TUB(1),
    OON(2),
    NDA(3),
    CBT(4),
    ROK(5),
    PTC(6),
    LMI(7),
    SOL(8),
    AEP(9),
    TFL(10);

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
