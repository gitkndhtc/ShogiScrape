package com.example.parser;

import com.example.GameResult;

import java.util.List;

public interface GameResultParser {
    public List<GameResult> parseResultsOnShogiAssoc(String resultPage, String gameDate);
}