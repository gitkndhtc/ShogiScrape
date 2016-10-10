package com.example;

import com.example.parser.GameResultParser;
import com.example.parser.GameResultParserImpl;
import com.example.service.GameResultRetriever;
import com.example.service.GameResultRetrieverImpl;
import com.example.util.DateGenerator;
import com.example.util.DateGeneratorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public WebClientProvider webClientProvider() {
        return new WebClientProviderImpl();
    }

    @Bean
    public ShogiAssocWebClient shogiAssocWebClient() {
        return new ShogiAssocWebClientImpl();
    }

    @Bean
    public GameResultParser gameResultParser() {
        return new GameResultParserImpl();
    }

    @Bean
    GameResultRetriever gameResultRetriever() {
        return new GameResultRetrieverImpl();
    }

    @Bean
    DateGenerator dateGenerator() {
        return new DateGeneratorImpl();
    }
}
