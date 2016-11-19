package com.example;

import com.example.util.FileReader;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;

import java.net.MalformedURLException;
import java.net.URL;


public class FakeWebClientProvider implements WebClientProvider {
    private static final String gameResultsPage = "http://www.shogi.or.jp/game/";
    private static final String resultsPageOfNHKCup = "http://www.shogi.or.jp/match/nhk/";
    private static final String resultsPageOfGalaxyTournament = "http://www.shogi.or.jp/match/ginga/";
    private WebClient webClient;

    @Override
    public WebClient getWebClient() {
        FileReader fileReader = new FileReader();
        String dummyHtml1 = fileReader.readAll("01_GameResult.html");
        String dummyHtml2 = fileReader.readAll("02_ResultOfNHKCup.html");
        String dummyHtml3 = fileReader.readAll("03_ResultOfGalaxyTournament.html");

        MockWebConnection mwc = new MockWebConnection();
        try {
            URL url1 = new URL(gameResultsPage);
            mwc.setResponse(url1, dummyHtml1);
            URL url2 = new URL(resultsPageOfNHKCup);
            mwc.setResponse(url2, dummyHtml2);
            URL url3 = new URL(resultsPageOfGalaxyTournament);
            mwc.setResponse(url3, dummyHtml3);
            this.webClient = new WebClient();
            webClient.setWebConnection(mwc);
            webClient.getOptions().setJavaScriptEnabled(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            return webClient;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
