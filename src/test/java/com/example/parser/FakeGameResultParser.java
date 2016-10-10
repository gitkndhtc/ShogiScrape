package com.example.parser;

import com.example.GameResult;

import java.util.ArrayList;
import java.util.List;

public class FakeGameResultParser implements GameResultParser{
    public List<String> parseResultsOnShogiAssoc_args = new ArrayList<>();
    public boolean parseResultsOnShogiAssoc_wasCalled = false;
    public List<GameResult> parseResultsOnShogiAssoc_returnValue = new ArrayList<>();

    @Override
    public List<GameResult> parseResultsOnShogiAssoc(String resultsPage, String gameDate) {
        parseResultsOnShogiAssoc_args.add(resultsPage);
        parseResultsOnShogiAssoc_args.add(gameDate);
        parseResultsOnShogiAssoc_wasCalled = true;

        return parseResultsOnShogiAssoc_returnValue;
    }
}
