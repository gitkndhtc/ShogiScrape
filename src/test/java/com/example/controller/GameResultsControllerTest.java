package com.example.controller;

import com.example.AppConfig;
import com.example.TestConfig;
import com.example.domain.GameResultTable;
import com.example.repository.GameResultsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AppConfig.class, TestConfig.class, JacksonAutoConfiguration.class})
@EnableAutoConfiguration
@ComponentScan("com.example")
public class GameResultsControllerTest {
    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ObjectMapper mapper;

    @Rule
    public final MockitoRule rule = MockitoJUnit.rule();

    private MockMvc mockMvc;

    @InjectMocks
    private GameResultsControllerImpl gameResultsController;

    @Mock
    private GameResultsRepository gameResultsRepository;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(this.gameResultsController)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .build();
    }

    @Test
    public void test_getGameResults_onSuccess() throws Exception {
        GameResultTable gameResultTable = new GameResultTable("行方尚史", "負", "羽生善治", "勝", "順位戦A級", "2016年9月14日", "");
        PageImpl<GameResultTable> expectedResultsPageImpl = new PageImpl<GameResultTable>(Collections.singletonList(gameResultTable));


        Mockito.when(gameResultsRepository.findAll(new PageRequest(0, 3)))
                .thenReturn(expectedResultsPageImpl);


        mockMvc.perform(get("/api/shogiGameResults?page=0&size=3")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .accept(APPLICATION_JSON_UTF8_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(expectedResultsPageImpl)));
/*
        String result = mockMvc.perform(get("/api/shogiGameResults?page=0&size=3")
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .accept(APPLICATION_JSON_UTF8_VALUE))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("result = " + result);
*/
    }
}
