package com.example.service;

import com.example.*;
import com.example.domain.GameResultTable;
import com.example.parser.FakeGameResultParser;
import com.example.parser.GameResultParser;
import com.example.repository.GameResultsRepository;
import com.example.util.DateGenerator;
import com.example.util.FakeDateGenerator;
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

    @Autowired
    DateGenerator dateGenerator;

    private FakeShogiAssocWebClient fakeShogiAssocWebClient;
    private FakeGameResultParser fakeGameResultParser;
    private FakeDateGenerator fakeDateGenerator;

    @Test
    public void test_retrieveGameResults_getSimplePage_onSuccess() {
        String expectedArg = "http://www.shogi.or.jp/game/";
        fakeShogiAssocWebClient = (FakeShogiAssocWebClient) shogiAssocWebClient;


        gameResultRetriever.retrieveGameResults();


        assertThat(fakeShogiAssocWebClient.getSimplePage_args, is(expectedArg));
        assertThat(fakeShogiAssocWebClient.geSimplePage_wasCalled, is(true));
    }

    @Test
    public void test_retrieveGameResults_dateIsToday_onSuccess() {
        fakeDateGenerator = (FakeDateGenerator) dateGenerator;


        gameResultRetriever.retrieveGameResults();


        assertThat(fakeDateGenerator.getYesterday_wasCalled, is(true));
    }

    @Test
    public void test_retrieveGameResults_parseResultsPage_onSuccess() {
        fakeShogiAssocWebClient = (FakeShogiAssocWebClient) shogiAssocWebClient;
        fakeShogiAssocWebClient.getSimplePage_returnValue = "<html><body>Master was undefeated<body><html>";
        fakeGameResultParser = (FakeGameResultParser) gameResultParser;
        fakeDateGenerator = (FakeDateGenerator) dateGenerator;
        fakeDateGenerator.getYesterday_returnValue= "2016年9月14日";


        gameResultRetriever.retrieveGameResults();


        assertThat(fakeGameResultParser.parseResultsOnShogiAssoc_args.get(0), is("<html><body>Master was undefeated<body><html>"));
        assertThat(fakeGameResultParser.parseResultsOnShogiAssoc_args.get(1), is("2016年9月14日"));
        assertThat(fakeGameResultParser.parseResultsOnShogiAssoc_wasCalled, is(true));
    }

    @Test
    public void test_retrieveGameResults_saveGameResults_onSuccess() {
        fakeGameResultParser = (FakeGameResultParser) gameResultParser;
        fakeGameResultParser.parseResultsOnShogiAssoc_returnValue =
                Collections.singletonList(
                        new GameResult(
                                "行方尚史", "負", "羽生善治", "勝", "順位戦A級", "2016年9月14日"
                        )
                );
        fakeDateGenerator = (FakeDateGenerator) dateGenerator;
        fakeDateGenerator.getYesterday_returnValue= "2016年9月14日";


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
        fakeGameResultParser = (FakeGameResultParser) gameResultParser;
        fakeGameResultParser.parseResultsOnShogiAssoc_returnValue = new ArrayList<>();


        gameResultRetriever.retrieveGameResults();
        List<GameResultTable> actualResults = gameResultsRepository.findAll();


        assertThat(actualResults.size(), is(0));
    }
}
