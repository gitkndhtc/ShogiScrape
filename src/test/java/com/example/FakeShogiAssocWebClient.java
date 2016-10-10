package com.example;

public class FakeShogiAssocWebClient implements ShogiAssocWebClient {
    public String getSimplePage_args = "Dummy URL";
    public boolean geSimplePage_wasCalled = false;
    public String getSimplePage_returnValue = "";

    @Override
    public String getSimplePage(String url) {
        getSimplePage_args = url;
        geSimplePage_wasCalled = true;

        return getSimplePage_returnValue;
    }
}
