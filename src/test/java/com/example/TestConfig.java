package com.example;

import com.example.parser.FakeGameResultParser;
import com.example.parser.GameResultParser;
import com.example.util.DateGenerator;
import com.example.util.FakeDateGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableSpringDataWebSupport
@Configuration
public class TestConfig {
    @Bean
    @Profile("unit")
    public WebClientProvider webClientProvider() {
        return new FakeWebClientProvider();
    }

    @Bean
    @Profile("GameResultRetriever")
    public ShogiAssocWebClient shogiAssocWebClient() {
        return new FakeShogiAssocWebClient();
    }

    @Bean
    @Profile("GameResultRetriever")
    public GameResultParser gameResultParser() {
        return new FakeGameResultParser();
    }

    @Bean
    @Profile("GameResultRetriever")
    public DateGenerator dateGenerator() {
        return new FakeDateGenerator();
    }

}