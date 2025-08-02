package com.tournament_helper.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Teams {
    // Ordem da sala custom.
    MBR(1),
    WKT(2),
    FTM(3),
    BTS(4),
    IFN(5),
    AUU(6),
    TCR(7),
    BST(8),
    SLA(9),
    FLV(10),
    FA1(11),
    FA2(12);

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