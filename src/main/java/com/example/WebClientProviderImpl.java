package com.example;

import com.gargoylesoftware.htmlunit.WebClient;
import org.springframework.stereotype.Component;

@Component
public class WebClientProviderImpl implements WebClientProvider {
    @Override
    public WebClient getWebClient() {
        WebClient webClient = new WebClient();
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        return webClient;
    }
}
