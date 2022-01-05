package com.sample.movie.controller;

import com.sample.movie.MovieLoverApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {MovieLoverApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:/application-test.yaml")
public class IndexControllerTest {
    @Autowired
    private TestRestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String RECOMMEND_INDEX_URL = "/movies-recommend";

    @Test
    @DisplayName("검색어 추천을 위한 인덱스를 생성하고 데이터를 색인한다.")
    public void given_movieData_then_index() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(RECOMMEND_INDEX_URL, new Object(), String.class);
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(responseEntity.getBody(), "OK");
    }


}
