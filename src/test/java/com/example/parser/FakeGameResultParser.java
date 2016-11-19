package com.example.parser;

import com.example.GameResult;
import com.example.domain.GameResultTable;

import java.util.ArrayList;
import java.util.List;

public class FakeGameResultParser implements GameResultParser{
    public List<String> parseResultsOnShogiAssoc_args = new ArrayList<>();
    public boolean parseResultsOnShogiAssoc_wasCalled = false;
    public List<GameResult> parseResultsOnShogiAssoc_returnValue = new ArrayList<>();

    public String parseResultsOnNHKCup_arg_resultPage = "This arg has not yet set";
    public GameResultTable parseResultsOnNHKCup_arg_gameResult;
    public boolean parseResultsOnNHKCup_wasCalled = false;
    public GameResultTable parseResultsOnNHKCup_returnValue = new GameResultTable(
            "先手","勝","後手","負","NHK杯","日付",null);

    public String parseResultsOnGalaxyTournament_arg_resultPage = "This arg has not yet set";
    public GameResultTable parseResultsOnGalaxyTournament_arg_gameResult;
    public boolean parseResultsOnGalaxyTournament_wasCalled = false;
    public GameResultTable parseResultsOnGalaxyTournament_returnValue = new GameResultTable(
            "先手","勝","後手","負","NHK杯","日付",null);

    public String parseResultsOnQueenTournament_arg_resultPage = "This arg has not yet set";
    public GameResultTable parseResultsOnQueenTournament_arg_gameResult;
    public boolean parseResultsOnQueenTournament_wasCalled = false;
    public GameResultTable parseResultsOnQueenTournament_returnValue = new GameResultTable(
            "先手","勝","後手","負","女流王将戦","日付",null);

    @Override
    public List<GameResult> parseResultsOnShogiAssoc(String resultsPage, String gameDate) {
        parseResultsOnShogiAssoc_args.add(resultsPage);
        parseResultsOnShogiAssoc_args.add(gameDate);
        parseResultsOnShogiAssoc_wasCalled = true;

        return parseResultsOnShogiAssoc_returnValue;
    }

    @Override
    public GameResultTable parseResultsOnNHKCup(String resultPage, GameResultTable gameResultTable) {
        parseResultsOnNHKCup_arg_resultPage = resultPage;
        parseResultsOnNHKCup_arg_gameResult = gameResultTable;
        parseResultsOnNHKCup_wasCalled = true;

        return parseResultsOnNHKCup_returnValue;
    }

    @Override
    public GameResultTable parseResultsOnGalaxyTournament(String resultPage, GameResultTable gameResultTable) {
        parseResultsOnGalaxyTournament_arg_resultPage = resultPage;
        parseResultsOnGalaxyTournament_arg_gameResult = gameResultTable;
        parseResultsOnGalaxyTournament_wasCalled = true;

        return parseResultsOnGalaxyTournament_returnValue;
    }

    @Override
    public GameResultTable parseResultsOnQueenTournament(String resultPage, GameResultTable gameResultTable) {
        parseResultsOnQueenTournament_arg_resultPage = resultPage;
        parseResultsOnQueenTournament_arg_gameResult = gameResultTable;
        parseResultsOnQueenTournament_wasCalled = true;

        return parseResultsOnQueenTournament_returnValue;
    }
}
