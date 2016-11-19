package com.example.service;

import com.example.GameResult;
import com.example.ShogiAssocWebClient;
import com.example.domain.GameResultTable;
import com.example.parser.GameResultParser;
import com.example.repository.GameResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.function.Function;

public class GameResultRetrieverImpl implements GameResultRetriever {
    @Autowired
    ShogiAssocWebClient shogiAssocWebClient;

    @Autowired
    GameResultParser gameResultParser;

    @Autowired
    GameResultsRepository gameResultsRepository;

    private static final String gameResultsURL = "http://www.shogi.or.jp/game/";
    private static final String NHKCupResultsURL = "http://www.shogi.or.jp/match/nhk/";
    private static final String GalaxyTournametResultsURL = "http://www.shogi.or.jp/match/ginga/";
    private static final String QueenTournametResultsURL = "http://www.igoshogi.net/shogi/Loushou/index.html";

    @Scheduled(initialDelay = 1000*30*1, fixedRate = 1000*3600*12)
    @Override
    public void retrieveGameResults() {
        String resultsPage = shogiAssocWebClient.getSimplePage(gameResultsURL);
        String targetDate = "9999年12月31日";

        gameResultParser.parseResultsOnShogiAssoc(resultsPage, targetDate)
                .stream()
                .peek(System.out::println)
                .map(mapToGameResultTable)
                .forEach(gameResultTable -> gameResultsRepository.save(gameResultTable));
    }

    @Scheduled(initialDelay = 1000*120*1, fixedRate = 1000*3600*12)
    @Override
    public void updateNHKCupResults() {
        String NHKCupResultsPage = shogiAssocWebClient.getSimplePage(NHKCupResultsURL);

        gameResultsRepository.findByTournamentNameContainingAndFirstMoverResult("NHK杯","")
                .stream()
                .peek(System.out::println)
                .map(gameResultTable ->
                        gameResultParser.parseResultsOnNHKCup(NHKCupResultsPage,gameResultTable))
                .peek(System.out::println)
                .forEach(gameResultTable ->
                        gameResultsRepository.save(gameResultTable));
    }

    @Scheduled(initialDelay = 1000*90*1, fixedRate = 1000*3600*12)
    @Override
    public void updateGalaxyTournamentResults() {
        String galaxyTournametResultsPage = shogiAssocWebClient.getSimplePage(GalaxyTournametResultsURL);

        gameResultsRepository.findByTournamentNameContainingAndFirstMoverResult("銀河","")
                .stream()
                .peek(System.out::println)
                .map(gameResultTable ->
                        gameResultParser.parseResultsOnGalaxyTournament(galaxyTournametResultsPage,gameResultTable))
                .peek(System.out::println)
                .forEach(gameResultTable ->
                        gameResultsRepository.save(gameResultTable));
    }

    @Scheduled(initialDelay = 1000*60*1, fixedRate = 1000*3600*12)
    @Override
    public void updateQueenTournamentResults() {
        String queenTournamentResultsPage = shogiAssocWebClient.getSimplePage(QueenTournametResultsURL);

        gameResultsRepository.findByTournamentNameContainingAndFirstMoverResult("女流王将","")
                .stream()
                .peek(System.out::println)
                .map(gameResultTable ->
                        gameResultParser.parseResultsOnQueenTournament(queenTournamentResultsPage,gameResultTable))
                .peek(System.out::println)
                .forEach(gameResultTable ->
                        gameResultsRepository.save(gameResultTable));
    }


    private Function<GameResult, GameResultTable> mapToGameResultTable =
            gameResult -> new GameResultTable(
                    gameResult.getFirstMover(),
                    gameResult.getFirstMoverResult(),
                    gameResult.getSecondMover(),
                    gameResult.getSecondMoverResult(),
                    gameResult.getTournamentName(),
                    gameResult.getGameDate(),
                    null
            );
}
