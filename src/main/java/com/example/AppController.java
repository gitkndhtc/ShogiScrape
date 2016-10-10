package com.example;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hitoshi on 2016/08/06.
 */

@RestController
@EnableAutoConfiguration
@RequestMapping("/api")
public class AppController {
    @RequestMapping(value = "/test", method = RequestMethod.GET)
    String home() {
        return "Hello World";
    }

}

