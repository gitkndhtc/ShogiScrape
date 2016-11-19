package com.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "GAME_RESULTS")
@IdClass(GameResultTableSummary.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResultTable {
    @Id
    private String firstMover;
    @Column
    private String firstMoverResult;
    @Id
    private String secondMover;
    @Column
    private String secondMoverResult;
    @Column
    private String tournamentName;
    @Id
    private String gameDate;
    @Column
    @Temporal(TemporalType.DATE)
    private Date createdDate;
}