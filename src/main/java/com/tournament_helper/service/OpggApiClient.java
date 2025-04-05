package com.tournament_helper.service;

import com.tournament_helper.domain.Match;
import com.tournament_helper.domain.Player;
import com.tournament_helper.domain.ResponseData;
import com.tournament_helper.domain.SearchedMatchDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class OpggApiClient {

    private final WebClient webClient;

    public List<Match> findAllMatches(String userId) {
        List<ResponseData> matchesData = webClient.get()
                .uri("/players/steam-{userId}/matches?page=1", userId)
                .retrieve()
                .bodyToFlux(ResponseData.class)
                .collectList()
                .block();
        try {
            if (matchesData == null) {
                throw new IllegalArgumentException("No matches found for " + userId);
            } else {
                return matchesData.get(0).getData();
            }
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("No matches found for " + userId);
        }
    }

    public List<SearchedMatchDetails> findMatchById(String matchId) {
        return  webClient.get()
                .uri("/matches/steam-{matchId}", matchId)
                .retrieve()
                .bodyToFlux(SearchedMatchDetails.class)
                .collectList()
                .block();
    }

    public List<Player> findPlayersByPlayerName(String playerName) {
        return webClient.get()
                .uri("/players/search?query={playerName}", playerName)
                .retrieve()
                .bodyToFlux(Player.class)
                .collectList()
                .block();
    }



    private Player filterPlayerByPlayerName(List<Player> players, String playerName) {
        players.forEach(p -> System.out.println(p.getUniqueDisplayName()));
        System.out.println("player name = " + playerName);
        return players.stream()
                .filter(p -> Objects.equals(p.getUniqueDisplayName(), playerName.replace("%23", "#")))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public String playerIdFound(String playerName) {
        List<Player> players = findPlayersByPlayerName(playerName);
        players.forEach(p -> System.out.println("Player: " + p.getUniqueDisplayName()));
        Player player = filterPlayerByPlayerName(players, playerName);
        if(player != null) {
            return player.getUserId();
        } else {
            throw new IllegalArgumentException("Player " + playerName + " not found");
        }
    }
}
