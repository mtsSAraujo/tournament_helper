package com.tournament_helper.controller.impl;

import com.tournament_helper.controller.OpggApi;
import com.tournament_helper.domain.Match;
import com.tournament_helper.domain.SearchedMatchDetails;
import com.tournament_helper.service.OpggApiClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OpggApiImpl implements OpggApi {

    private final OpggApiClient opggApiClient;

    @Override
    public List<Match> findAllMatches(String userId) {
        var matches = opggApiClient.findAllMatches(userId);
        System.out.println(matches);
        return null;
    }

    @Override
    public String findPlayerId(String playerName) {
        String playerId = opggApiClient.playerIdFound(playerName);
        var matches = opggApiClient.findAllMatches(playerId);
        return playerId;
    }

    @Override
    public List<SearchedMatchDetails> findMatchById(String matchId) {
        System.out.println(matchId);
        var match = opggApiClient.findMatchById(matchId);
        return match;
    }
}
