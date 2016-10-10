package com.example.controller;

import com.example.domain.GameResultTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameResultsController {
    Page<GameResultTable> getGameResults(Pageable pageable);
}
