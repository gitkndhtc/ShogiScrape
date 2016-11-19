package com.example.service;

import com.example.*;
import com.example.domain.GameResultTable;
import com.example.parser.FakeGameResultParser;
import com.example.parser.GameResultParser;
import com.example.repository.GameResultsRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@EnableAutoConfiguration
@ComponentScan("com.example")
@ActiveProfiles("GameResultRetriever")
public class GameResultRetrieverTest {
    @Autowired
    GameResultRetriever gameResultRetriever;

    @Autowired
    ShogiAssocWebClient shogiAssocWebClient;

    @Autowired
    GameResultParser gameResultParser;

    @Autowired
    GameResultsRepository gameResultsRepository;

    private FakeShogiAssocWebClient fakeShogiAssocWebClient;
    private FakeGameResultParser fakeGameResultParser;

    @Before
    public void setUp() {
        fakeShogiAssocWebClient = (FakeShogiAssocWebClient) shogiAssocWebClient;
        fakeGameResultParser = (FakeGameResultParser) gameResultParser;
    }
    @After
    public void tearDown() {
        gameResultsRepository.deleteAll();
    }

    @Test
    public void test_retrieveGameResults_getSimplePage_onSuccess() {
        String expectedArg = "http://www.shogi.or.jp/game/";


        gameResultRetriever.retrieveGameResults();


        assertThat(fakeShogiAssocWebClient.getSimplePage_args, is(expectedArg));
        assertThat(fakeShogiAssocWebClient.geSimplePage_wasCalled, is(true));
    }

    @Test
    public void test_retrieveGameResults_parseResultsPage_onSuccess() {
        fakeShogiAssocWebClient.getSimplePage_returnValue = "<html><body>Master was undefeated<body><html>";


        gameResultRetriever.retrieveGameResults();


        assertThat(fakeGameResultParser.parseResultsOnShogiAssoc_args.get(0), is("<html><body>Master was undefeated<body><html>"));
        assertThat(fakeGameResultParser.parseResultsOnShogiAssoc_wasCalled, is(true));
    }

    @Test
    public void test_retrieveGameResults_saveGameResults_onSuccess() {
        fakeGameResultParser.parseResultsOnShogiAssoc_returnValue =
                Collections.singletonList(
                        new GameResult(
                                "行方尚史", "負", "羽生善治", "勝", "順位戦A級", "2016年9月14日"
                        )
                );


        gameResultRetriever.retrieveGameResults();
        List<GameResultTable> actualResults = gameResultsRepository.findAll();


        assertThat(actualResults.get(0).getFirstMover(), is("行方尚史"));
        assertThat(actualResults.get(0).getFirstMoverResult(), is("負"));
        assertThat(actualResults.get(0).getSecondMover(), is("羽生善治"));
        assertThat(actualResults.get(0).getSecondMoverResult(), is("勝"));
        assertThat(actualResults.get(0).getTournamentName(), is("順位戦A級"));
        assertThat(actualResults.get(0).getGameDate(), is("2016年9月14日"));
    }

    @Test
    public void test_retrieveGameResults_gameResultsIsEmpty() {
        fakeGameResultParser.parseResultsOnShogiAssoc_returnValue = new ArrayList<>();


        gameResultRetriever.retrieveGameResults();
        List<GameResultTable> actualResults = gameResultsRepository.findAll();


        assertThat(actualResults.size(), is(0));
    }

    @Test
    public void test_updateNHKCupResults_getPage_onSuccess() {
        String expectedArg = "http://www.shogi.or.jp/match/nhk/";


        gameResultRetriever.updateNHKCupResults();


        assertThat(fakeShogiAssocWebClient.getSimplePage_args, is(expectedArg));
        assertThat(fakeShogiAssocWebClient.geSimplePage_wasCalled, is(true));
    }

    @Test
    public void test_updateNHKCupResults_getUnsatisfiedResult() {
        fakeShogiAssocWebClient.getSimplePage_returnValue = "<html><body>Master was undefeated<body><html>";
        GameResultTable gameResultTable = new GameResultTable("船江恒平", "", "近藤誠也", "", "NHK杯", "2016年4月3日", null);
        gameResultsRepository.save(gameResultTable);


        gameResultRetriever.updateNHKCupResults();


        assertThat(fakeGameResultParser.parseResultsOnNHKCup_arg_resultPage, is("<html><body>Master was undefeated<body><html>"));
        assertThat(fakeGameResultParser.parseResultsOnNHKCup_arg_gameResult, is(new GameResultTable("船江恒平", "", "近藤誠也", "", "NHK杯", "2016年4月3日", null)));
        assertThat(fakeGameResultParser.parseResultsOnNHKCup_wasCalled, is(true));
    }

    @Test
    public void test_updateNHKCupResults_getUpdatedResult() {
        fakeShogiAssocWebClient.getSimplePage_returnValue = "<html><body>Master was undefeated<body><html>";
        GameResultTable gameResultTable = new GameResultTable("ドラえもん", "", "野比のび太", "", "NHK杯", "2201年1月3日", null);
        gameResultsRepository.save(gameResultTable);
        GameResultTable expectedResult = new GameResultTable("船江恒平", "勝", "近藤誠也", "負", "NHK杯", "2016年4月3日", null);
        fakeGameResultParser.parseResultsOnNHKCup_returnValue = expectedResult;


        gameResultRetriever.updateNHKCupResults();
        List<GameResultTable> actualResult = gameResultsRepository.findByTournamentNameContainingAndFirstMoverResult("NHK杯", "勝");
        System.out.println("actualResult = " + actualResult);

        assertThat(actualResult.size(), is(1));
        assertThat(actualResult.get(0), is(expectedResult));
    }

    @Test
    public void test_updateGalaxyTournamentResults_getPage_onSuccess() {
        String expectedArg = "http://www.shogi.or.jp/match/ginga/";


        gameResultRetriever.updateGalaxyTournamentResults();


        assertThat(fakeShogiAssocWebClient.getSimplePage_args, is(expectedArg));
        assertThat(fakeShogiAssocWebClient.geSimplePage_wasCalled, is(true));
    }

    @Test
    public void test_updateGalaxyTournamentResults_getUnsatisfiedResult() {
        fakeShogiAssocWebClient.getSimplePage_returnValue = "<html><body>Master was undefeated<body><html>";
        GameResultTable gameResultTable = new GameResultTable("門倉啓太", "", "今泉健司", "", "銀河戦 Eブロック", "2016年11月15日", null);
        gameResultsRepository.save(gameResultTable);


        gameResultRetriever.updateGalaxyTournamentResults();


        assertThat(fakeGameResultParser.parseResultsOnGalaxyTournament_arg_resultPage, is("<html><body>Master was undefeated<body><html>"));
        assertThat(fakeGameResultParser.parseResultsOnGalaxyTournament_arg_gameResult, is(gameResultTable));
        assertThat(fakeGameResultParser.parseResultsOnGalaxyTournament_wasCalled, is(true));
    }

    @Test
    public void test_updateGalaxyTournamentResults_getUpdatedResult() {
        fakeShogiAssocWebClient.getSimplePage_returnValue = "<html><body>Master was undefeated<body><html>";
        GameResultTable gameResultTable = new GameResultTable("ドラえもん", "", "野比のび太", "", "銀河戦 Eブロック", "2201年1月3日", null);
        gameResultsRepository.save(gameResultTable);
        GameResultTable expectedResult = new GameResultTable("門倉啓太", "負", "今泉健司", "勝", "銀河戦 Eブロック", "2016年11月15日", null);
        fakeGameResultParser.parseResultsOnGalaxyTournament_returnValue = expectedResult;


        gameResultRetriever.updateGalaxyTournamentResults();
        List<GameResultTable> actualResult = gameResultsRepository.findByTournamentNameContainingAndFirstMoverResult("銀河", "負");
        System.out.println("actualResult = " + actualResult);

        assertThat(actualResult.size(), is(1));
        assertThat(actualResult.get(0), is(expectedResult));
    }
}
