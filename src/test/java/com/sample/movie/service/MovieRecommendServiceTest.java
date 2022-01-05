package com.sample.movie.service;

import com.sample.movie.MovieLoverApplication;
import com.sample.movie.helper.MovieIndexTestHelper;
import com.sample.movie.model.MovieData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {MovieLoverApplication.class})
@SpringBootTest(properties = "spring.config.location=classpath:/application-test.yaml")
public class MovieRecommendServiceTest {

    @Autowired
    private MovieRecommendService movieRecommendService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @BeforeAll
    public static void setUp(@Autowired MovieIndexTestHelper movieIndexTestHelper) throws IOException {
        movieIndexTestHelper.setUp();
    }

    @Test()
    @DisplayName("검색어를 아무것도 입력하지 않았을 때는 추천검색어 5개를 보여준다.")
    public void given_empty_query_when_search_then_return_any_five_result() throws IOException {
        List<MovieData> result = movieRecommendService.getRandomRecommend();
        Assertions.assertTrue(result != null && result.size() == 5);
    }

    @Test()
    @DisplayName("'나'로 검색 시 영화제목에서 '나'가 포함된 추천 검색이 최대 5개가 추천되어야 한다.")
    public void given_나_when_search_then_return_movie_name_include_나() throws IOException {
        String givenQuery = "나";
        List<MovieData> result = movieRecommendService.getMovieRecommendByRequest(givenQuery);
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
        List<MovieData> result = movieRecommendService.getMovieRecommendByRequest(givenQuery);
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
        List<MovieData> result = movieRecommendService.getRandomRecommend();
        Assertions.assertTrue(result != null && result.size() == 5);
        for (int i = 0; i < result.size(); i++) {
            if (i == 0) continue;
            int previousMovieNameLength = result.get(i - 1).getMovieName().length();
            int currentMovieNameLength = result.get(i).getMovieName().length();
            Assertions.assertTrue(previousMovieNameLength <= currentMovieNameLength);
        }
    }
}
