package com.example;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ShogiAssocWebClientImpl implements ShogiAssocWebClient {
    @Autowired
    WebClientProvider webClientProvider;

    @Override
    public String getSimplePage(String url) {
        WebClient webClient = webClientProvider.getWebClient();
        try {
            HtmlPage page = webClient.getPage(url);

            return page.getWebResponse().getContentAsString();
        } catch (IOException e) {
            e.printStackTrace();

            return "error";
        } catch (IllegalStateException e) {
            e.printStackTrace();

            return "error";
        }
    }
}
