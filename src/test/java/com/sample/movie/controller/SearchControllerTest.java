package com.sample.movie.controller;

import com.sample.movie.MovieLoverApplication;
import com.sample.movie.dto.MovieRecommendResponse;
import com.sample.movie.helper.MovieIndexTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {MovieLoverApplication.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.config.location=classpath:/application-test.yaml")
public class SearchControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String RECOMMEND_SEARCH_URL = "/movies-recommend";

    @BeforeAll
    public static void setUp(@Autowired MovieIndexTestHelper movieIndexTestHelper) throws IOException {
        movieIndexTestHelper.setUp();
    }

    @Test
    @DisplayName("검색어를 아무것도 입력하지 않았을 때는 추천검색어 5개를 보여준다.")
    public void given_empty_query_when_search_then_return_any_five_result() {
        ResponseEntity<MovieRecommendResponse[]> responseEntity = restTemplate.getForEntity(RECOMMEND_SEARCH_URL, MovieRecommendResponse[].class);
        List<MovieRecommendResponse> result = Arrays.asList(responseEntity.getBody());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(result != null && result.size() == 5);
    }

    @Test()
    @DisplayName("'나'로 검색 시 영화제목에서 '나'가 포함된 추천 검색이 최대 5개가 추천되어야 한다.")
    public void given_나_when_search_then_return_movie_name_include_나() {
        String givenQuery = "나";
        ResponseEntity<MovieRecommendResponse[]> responseEntity = restTemplate.getForEntity(RECOMMEND_SEARCH_URL + "?q=" + givenQuery, MovieRecommendResponse[].class);
        List<MovieRecommendResponse> result = Arrays.asList(responseEntity.getBody());
        Assertions.assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        Assertions.assertTrue(result != null && result.size() <= 5);
        result.forEach((r) -> {
            logger.info("search result in test:: " + r.getMovieName());
            Assertions.assertTrue(r.getMovieName().indexOf("나") != -1);
        });
    }

    @Test()
    @DisplayName("사용자가 검색어를 입력시 추천 검색어는 검색어 길이가 짧은 순으로 정렬되어야 합니다.")
    public void given_not_empty_query_when_search_then_result_should_movieName_sort_asc() throws IOException {
        String givenQuery = "다";
        ResponseEntity<MovieRecommendResponse[]> responseEntity = restTemplate.getForEntity(RECOMMEND_SEARCH_URL + "?q=" + givenQuery, MovieRecommendResponse[].class);
        List<MovieRecommendResponse> result = Arrays.asList(responseEntity.getBody());
        Assertions.assertTrue(result != null && result.size() == 5);
        for (int i = 0; i < result.size(); i++) {
            if (i == 0) continue;
            int previousMovieNameLength = result.get(i - 1).getMovieName().length();
            int currentMovieNameLength = result.get(i).getMovieName().length();
            Assertions.assertTrue(previousMovieNameLength <= currentMovieNameLength);
        }
    }

    @Test()
    @DisplayName("사용자가 검색어를 입력하지 않을 시 추천 검색어는 검색어 길이가 짧은 순으로 정렬되어야 합니다.")
    public void given_empty_query_when_search_then_result_should_movieName_sort_asc() throws IOException {
        ResponseEntity<MovieRecommendResponse[]> responseEntity = restTemplate.getForEntity(RECOMMEND_SEARCH_URL, MovieRecommendResponse[].class);
        List<MovieRecommendResponse> result = Arrays.asList(responseEntity.getBody());
        Assertions.assertTrue(result != null && result.size() == 5);
        for (int i = 0; i < result.size(); i++) {
            if (i == 0) continue;
            int previousMovieNameLength = result.get(i - 1).getMovieName().length();
            int currentMovieNameLength = result.get(i).getMovieName().length();
            Assertions.assertTrue(previousMovieNameLength <= currentMovieNameLength);
        }
    }

}
