package com.tournament_helper.service;

import com.tournament_helper.domain.Match;
import com.tournament_helper.domain.SearchedMatchDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MatchService {

    private final OpggApiClient opggApiClient;

    public List<SearchedMatchDetails> findMatchById(String matchId) {
        return opggApiClient.findMatchById(matchId);
    }

    public List<Match> findAllMatches(String userId) {
        return opggApiClient.findAllMatches(userId);
    }
}
