package com.example.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateGeneratorImpl implements DateGenerator {
    @Override
    public String getYesterday() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("YYYY年M月d日");
        return LocalDate.now().minusDays(1).format(dateFormatter);
    }
}
