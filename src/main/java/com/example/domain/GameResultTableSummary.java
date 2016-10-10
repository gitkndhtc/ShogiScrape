package com.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResultTableSummary implements Serializable{
    @Column
    private String firstMover;
    @Column
    private String secondMover;
    @Column
    private String gameDate;
}
