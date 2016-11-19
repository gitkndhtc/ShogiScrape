package com.example.parser;

import com.example.AppConfig;
import com.example.GameResult;
import com.example.TestConfig;
import com.example.domain.GameResultTable;
import com.example.util.FileReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@EnableAutoConfiguration
@ComponentScan("com.example")
public class GameResultParserTest {
    @Autowired
    GameResultParserImpl gameResultParser;

    @Test
    public void test_parseResultsOnShogiAssoc_withSingleGame() {
        GameResult expectedResult = new GameResult("行方尚史", "負", "羽生善治", "勝", "順位戦A級", "2016年9月15日");
        FileReader fileReader = new FileReader();
        String resultsPageOnShogiAssoc = fileReader.readAll("01_SingleGameResult.html");


        List<GameResult> actualResults =
                gameResultParser.parseResultsOnShogiAssoc(
                        resultsPageOnShogiAssoc, "2016年9月15日");


        assertThat(actualResults.get(0), is(expectedResult));
    }

    @Test
    public void test_parseResultsOnShogiAssoc_withSeveralGamesOnTargetDate() {
        GameResult expectedFirstResult = new GameResult("森下　卓", "勝", "渡辺正和", "負", "朝日杯将棋オープン戦 一次予選", "2016年9月14日");
        GameResult expectedLastResult = new GameResult("清水市代", "負", "伊藤沙恵", "勝", "倉敷藤花", "2016年9月14日");
        FileReader fileReader = new FileReader();
        String resultsPageOnShogiAssoc = fileReader.readAll("01_GameResult.html");


        List<GameResult> actualResults =
                gameResultParser.parseResultsOnShogiAssoc(
                        resultsPageOnShogiAssoc, "2016年9月14日");
        Integer actualListSize = actualResults.size();


        assertThat(actualListSize, is(3));
        assertThat(actualResults.get(0), is(expectedFirstResult));
        assertThat(actualResults.get(actualListSize - 1), is(expectedLastResult));
    }

    @Test
    public void test_parseResultsOnShogiAssoc_with2DaysGame() {
        GameResult expectedFirstResult = new GameResult("羽生善治", "勝", "木村一基", "負", "王位戦 第7局", "2016年9月26日,2016年9月27日");
        FileReader fileReader = new FileReader();
        String resultsPageOnShogiAssoc = fileReader.readAll("01_GameResult.html");


        List<GameResult> actualResults =
                gameResultParser.parseResultsOnShogiAssoc(
                        resultsPageOnShogiAssoc, "2016年9月27日");
        Integer actualListSize = actualResults.size();


        assertThat(actualListSize, is(1));
        assertThat(actualResults.get(0), is(expectedFirstResult));
    }

    @Test
    public void test_parseResultsOnShogiAssoc_withAllGames() {
        FileReader fileReader = new FileReader();
        String resultsPageOnShogiAssoc = fileReader.readAll("01_GameResult.html");


        List<GameResult> actualResults =
                gameResultParser.parseResultsOnShogiAssoc(resultsPageOnShogiAssoc, "9999年12月31日");
        Integer actualListSize = actualResults.size();


        assertThat(actualListSize, is(12));
    }

    @Test
    public void test_parseResultsOnNHKCup_withSuccessfulResult() {
        FileReader fileReader = new FileReader();
        String resultsPageOnNHKCup = fileReader.readAll("02_ResultOfNHKCup.html");
        GameResultTable gameResultTable = new GameResultTable("佐藤天彦","","山崎隆之","","NHK杯","2016年11月13日",null);
        GameResultTable expectedResults = new GameResultTable("佐藤天彦","勝","山崎隆之","負","NHK杯","2016年11月13日",null);


        GameResultTable actualResults =
                gameResultParser.parseResultsOnNHKCup(resultsPageOnNHKCup,gameResultTable);


        assertThat(actualResults, is(expectedResults));
    }

    @Test
    public void test_parseResultsOnNHKCup_withFailureResult() {
        FileReader fileReader = new FileReader();
        String resultsPageOnNHKCup = fileReader.readAll("02_ResultOfNHKCup.html");
        GameResultTable gameResultTable = new GameResultTable("Dummy1","","Dummy2","","NHK杯","2016年4月3日",null);


        GameResultTable actualResult =
                gameResultParser.parseResultsOnNHKCup(resultsPageOnNHKCup,gameResultTable);


        assertThat(actualResult, is(gameResultTable));
    }

    @Test
    public void test_parseResultsOnGalaxyTournament_withSuccessfulResults() {
        FileReader fileReader = new FileReader();
        String resultsPageOnGalaxyTournament = fileReader.readAll("03_ResultOfGalaxyTournament.html");
        GameResultTable gameResultTable = new GameResultTable("門倉啓太", "", "今泉健司", "", "銀河戦 Eブロック", "2016年11月15日", null);
        GameResultTable expectedResults = new GameResultTable("門倉啓太", "負", "今泉健司", "勝", "銀河戦 Eブロック", "2016年11月15日", null);

        GameResultTable actualResults =
                gameResultParser.parseResultsOnGalaxyTournament(resultsPageOnGalaxyTournament,gameResultTable);


        assertThat(actualResults, is(expectedResults));
    }

    @Test
    public void test_parseResultsOnGalaxyTournament_withFailureResult() {
        FileReader fileReader = new FileReader();
        String resultsPageOnGalaxyTournament = fileReader.readAll("03_ResultOfGalaxyTournament.html");
        GameResultTable gameResultTable = new GameResultTable("Dummy1","","Dummy2","","銀河戦 Eブロック","2016年11月15日",null);


        GameResultTable actualResult =
                gameResultParser.parseResultsOnGalaxyTournament(resultsPageOnGalaxyTournament,gameResultTable);


        assertThat(actualResult, is(gameResultTable));
    }


/*
    @Test
    public void test_parseResultsOnShogiAssoc_withSeveralGames() {
        GameResult expectedFirstResult = new GameResult("行方尚史", "負", "羽生善治", "勝", "順位戦A級", "9月15日");
        GameResult expectedSecondResult = new GameResult("飯島栄治", "勝", "松尾　歩", "負", "順位戦B級1組", "9月15日");
        GameResult expectedLastResult = new GameResult("清水市代", "負", "伊藤沙恵", "勝", "倉敷藤花", "9月14日");
        FileReader fileReader = new FileReader();
        String resultsPageOnShogiAssoc = fileReader.readAll("01_GameResult.html");


        List<GameResult> actualResults =
                gameResultParser.parseResultsOnShogiAssoc(
                        resultsPageOnShogiAssoc, ""
                );
        Integer actualListSize = actualResults.size();


        assertThat(actualListSize, is(11));
        assertThat(actualResults.get(0), is(expectedFirstResult));
        assertThat(actualResults.get(1), is(expectedSecondResult));
        assertThat(actualResults.get(actualListSize - 1), is(expectedLastResult));
    }
*/

    @Test
    public void test_parseResultsOnShogiAssoc_withEmptyResult() {
        String failedPage = "<html><body>Dummy Html</body></html>";


        List<GameResult> actualResults =
                gameResultParser.parseResultsOnShogiAssoc(
                        failedPage, "2016年9月14日"
                );


        assertThat(actualResults.size(), is(0));
    }
}
