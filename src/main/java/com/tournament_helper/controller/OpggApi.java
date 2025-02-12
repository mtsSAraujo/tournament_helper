package com.tournament_helper.controller;

import com.tournament_helper.domain.Match;
import com.tournament_helper.domain.SearchedMatchDetails;

import java.util.List;

public interface OpggApi {

    List<Match> findAllMatches(String userId);

    String findPlayerId(String playerName);

    List<SearchedMatchDetails> findMatchById(String matchId);
}
