package com.example.repository;

import com.example.domain.GameResultTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameResultsRepository extends JpaRepository<GameResultTable, Integer> {
    List<GameResultTable> findByTournamentNameContainingAndFirstMoverResult(String tournamentName, String firstMoverResult);
}
