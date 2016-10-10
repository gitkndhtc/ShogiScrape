package com.example;

import com.example.util.FileReader;
import com.gargoylesoftware.htmlunit.MockWebConnection;
import com.gargoylesoftware.htmlunit.WebClient;

import java.net.MalformedURLException;
import java.net.URL;


public class FakeWebClientProvider implements WebClientProvider {
    private static final String gameResultsPage = "http://www.shogi.or.jp/game/";
    private WebClient webClient;

    @Override
    public WebClient getWebClient() {
        FileReader fileReader = new FileReader();
        String dummyHtml = fileReader.readAll("01_GameResult.html");

        MockWebConnection mwc = new MockWebConnection();
        try {
            URL url = new URL(gameResultsPage);
            mwc.setResponse(url, dummyHtml);
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
