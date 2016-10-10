package com.example.util;

public class FakeDateGenerator implements DateGenerator {
    public boolean getYesterday_wasCalled = false;
    public String getYesterday_returnValue = "YYYY年M月d日";

    @Override
    public String getYesterday() {
        getYesterday_wasCalled = true;
        return getYesterday_returnValue;
    }
}
