package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class})
@EnableAutoConfiguration
@ComponentScan("com.example")
@ActiveProfiles("unit")
public class ShogiAssocWebClientTest {
    private static final String gameResultsPage = "http://www.shogi.or.jp/game/";

    @Autowired
    ShogiAssocWebClient shogiAssocWebClient;


    @Test
    public void test_getSimplePage_whenSuccessful() {
        String resultPage = shogiAssocWebClient.getSimplePage(gameResultsPage);


        System.out.println("resultPage = " + resultPage);
        assertThat(resultPage, is(notNullValue()));
    }

    @Test
    public void test_getSimplePage_whenFailure() {
        String resultPage = shogiAssocWebClient.getSimplePage("http://bad.url/page");


        System.out.println("resultPage = " + resultPage);
        assertThat(resultPage, is("error"));
    }
}
