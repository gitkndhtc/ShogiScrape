package com.example.controller;

import com.example.domain.GameResultTable;
import com.example.repository.GameResultsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/shogiGameResults")
public class GameResultsControllerImpl implements GameResultsController {
    @Autowired
    GameResultsRepository gameResultsRepository;

    @GetMapping
    @Override
    public Page<GameResultTable> getGameResults(@PageableDefault Pageable pageable) {
        return gameResultsRepository.findAll(pageable);
    }
}
