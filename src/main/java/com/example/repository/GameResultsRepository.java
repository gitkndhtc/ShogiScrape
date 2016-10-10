package com.example.repository;

import com.example.domain.GameResultTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultsRepository extends JpaRepository<GameResultTable, Integer> {
}
