package com.tournament_helper.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@AllArgsConstructor
public class PlayerService {

    private final OpggApiClient opggApiClient;

    public String findPlayerId(String playerName) {
        return opggApiClient.playerIdFound(playerName);
    }
}
