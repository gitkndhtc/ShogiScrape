package com.example.service;

import com.example.GameResult;
import com.example.ShogiAssocWebClient;
import com.example.domain.GameResultTable;
import com.example.parser.GameResultParser;
import com.example.repository.GameResultsRepository;
import com.example.util.DateGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.function.Function;

public class GameResultRetrieverImpl implements GameResultRetriever {
    @Autowired
    ShogiAssocWebClient shogiAssocWebClient;

    @Autowired
    GameResultParser gameResultParser;

    @Autowired
    GameResultsRepository gameResultsRepository;

    @Autowired
    DateGenerator dateGenerator;

    private static final String gameResultsPage = "http://www.shogi.or.jp/game/";

    @Scheduled(initialDelay = 5000, fixedRate = 12*3600*1000)
    @Override
    public void retrieveGameResults() {
        String resultsPage = shogiAssocWebClient.getSimplePage(gameResultsPage);
        String yesterday = dateGenerator.getYesterday();

        List<GameResult> gameResults =
                gameResultParser.parseResultsOnShogiAssoc(resultsPage, yesterday);
        System.out.println("gameResults = " + gameResults);
        gameResults.stream()
                .map(mapToGameResultTable)
                .forEach(gameResultTable -> gameResultsRepository.save(gameResultTable));
    }

    private Function<GameResult, GameResultTable> mapToGameResultTable =
            gameResult -> new GameResultTable(
                    gameResult.getFirstMover(),
                    gameResult.getFirstMoverResult(),
                    gameResult.getSecondMover(),
                    gameResult.getSecondMoverResult(),
                    gameResult.getTournamentName(),
                    gameResult.getGameDate(),
                    ""
            );
}
