package com.example.repository;

import com.example.domain.GameResultTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@EnableAutoConfiguration
@ComponentScan("com.example")
public class GameResultsRepositoryTest {
    @Autowired
    GameResultsRepository gameResultsRepository;

    @Test
    public void test_Save() {
        GameResultTable gameResultTable = new GameResultTable("行方尚史", "負", "羽生善治", "勝", "順位戦A級", "9月15日", "");


        gameResultsRepository.save(gameResultTable);
        List<GameResultTable> actualResults = gameResultsRepository.findAll();
        Integer actualListSize = actualResults.size();


        assertThat(actualListSize, is(1));
        assertThat(actualResults.get(0).getFirstMover(), is(gameResultTable.getFirstMover()));
        assertThat(actualResults.get(0).getFirstMoverResult(), is(gameResultTable.getFirstMoverResult()));
        assertThat(actualResults.get(0).getSecondMover(), is(gameResultTable.getSecondMover()));
        assertThat(actualResults.get(0).getSecondMoverResult(), is(gameResultTable.getSecondMoverResult()));
        assertThat(actualResults.get(0).getTournamentName(), is(gameResultTable.getTournamentName()));
        assertThat(actualResults.get(0).getGameDate(), is(gameResultTable.getGameDate()));
    }
    @Test
    public void test_Save_whenSameRecordExists() {
        GameResultTable gameResultTable = new GameResultTable("行方尚史", "負", "羽生善治", "勝", "順位戦A級", "9月15日", "2016/10/05");
        GameResultTable gameResultTable2 = new GameResultTable("行方尚史", "負", "羽生善治", "勝", "順位戦A級", "9月15日", "2016/10/06");


        gameResultsRepository.save(gameResultTable);
        gameResultsRepository.save(gameResultTable2);
        List<GameResultTable> actualResults = gameResultsRepository.findAll();
        Integer actualListSize = actualResults.size();


        assertThat(actualListSize, is(1));
        assertThat(actualResults.get(0).getFirstMover(), is(gameResultTable.getFirstMover()));
        assertThat(actualResults.get(0).getFirstMoverResult(), is(gameResultTable.getFirstMoverResult()));
        assertThat(actualResults.get(0).getSecondMover(), is(gameResultTable.getSecondMover()));
        assertThat(actualResults.get(0).getSecondMoverResult(), is(gameResultTable.getSecondMoverResult()));
        assertThat(actualResults.get(0).getTournamentName(), is(gameResultTable.getTournamentName()));
        assertThat(actualResults.get(0).getGameDate(), is(gameResultTable.getGameDate()));
    }
}
