package com.example;

import lombok.Value;

@Value
public class GameResult {
    private String firstMover;
    private String firstMoverResult;
    private String secondMover;
    private String secondMoverResult;
    private String tournamentName;
    private String gameDate;
}