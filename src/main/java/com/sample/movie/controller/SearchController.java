package com.sample.movie.controller;

import com.sample.movie.service.MovieRecommendService;
import com.sample.movie.dto.MovieRecommendResponse;
import com.sample.movie.model.MovieData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(tags = "search")
public class SearchController {

    @Autowired
    private MovieRecommendService movieRecommendService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ApiOperation(value = "영화 데이터를 검색한다.", notes = "검색 파라미터로 q를 넣으면 질의에 대해서 검색한다. 단, q가 없을 경우 랜덤하게 추천 검색을 한다.")
    @ApiImplicitParams({@ApiImplicitParam(name = "q", value = "검색어")})
    @GetMapping("/movies-recommend")
    public List<MovieRecommendResponse> searchMovies(@RequestParam(required = false) String q) throws IOException {
        List<MovieData> recommendMovieList;
        if (q == null || q.trim().equals("")) {
            // 검색 조건이 없을 경우 랜덤 추천
            recommendMovieList = movieRecommendService.getRandomRecommend();
        } else {
            q = q.trim();
            // 성능상 저하를 막기 위해서 검색어는 앞에서 부터 200글자까지 검색하도록 한다.
            if (q.length() > 200) {
                q = q.substring(0, 200);
            }
            recommendMovieList = movieRecommendService.getMovieRecommendByRequest(q);
        }

        if (recommendMovieList != null && recommendMovieList.size() > 0) {
            return new MovieRecommendResponse()
                    .convertRecommendMovieList(recommendMovieList).stream()
                    .sorted(Comparator.comparingInt(x -> x.getMovieName().length()))
                    .collect(Collectors.toList());
        }
        return null;
    }

}
