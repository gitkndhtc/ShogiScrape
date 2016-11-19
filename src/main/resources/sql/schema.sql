CREATE TABLE GAME_RESULTS (
    first_mover VARCHAR(20),
    first_mover_result VARCHAR(10),
    second_mover VARCHAR(20),
    second_mover_result VARCHAR(10),
    tournament_name VARCHAR(50),
    game_date VARCHAR(20),
    created_date DATE NOT NULL DEFAULT now(),
    PRIMARY KEY(first_mover,second_mover,game_date)
);