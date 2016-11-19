package com.example.parser;

import com.example.GameResult;
import com.example.domain.GameResultTable;

import java.util.List;

public interface GameResultParser {
    public List<GameResult> parseResultsOnShogiAssoc(String resultPage, String gameDate);
    public GameResultTable parseResultsOnNHKCup(String resultPage, GameResultTable gameResultTable);
    public GameResultTable parseResultsOnGalaxyTournament(String resultPage, GameResultTable gameResultTable);
    public GameResultTable parseResultsOnQueenTournament(String resultPage, GameResultTable gameResultTable);
}