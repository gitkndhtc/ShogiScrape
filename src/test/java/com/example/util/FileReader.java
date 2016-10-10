package com.example.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class FileReader {
    public String readAll(final String targetFile) {
        try {
            return Files.lines(
                    Paths.get(
                            this.getClass().getClassLoader().getResource(targetFile).getPath()
                    ),
                    Charset.forName("UTF-8")
            )
                    .collect(Collectors.joining(System.getProperty("line.separator")));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
