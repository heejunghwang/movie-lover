package com.sample.movie.service;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.google.gson.Gson;
import com.sample.movie.model.MovieData;
import com.sample.movie.repository.ElasticSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MovieRecommendService {

    @Autowired
    private ElasticSearchRepository elasticSearchRepository;

    public static final String MOVIES_RECOMMEND_ALIAS = "movies-recommend";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<MovieData> getRandomRecommend() throws IOException {
        SearchRequest request = new SearchRequest.Builder()
                .index(this.MOVIES_RECOMMEND_ALIAS)
                .query(q -> q.functionScore(
                        fs -> fs.functions(
                                f -> f.randomScore(
                                        rs -> rs.seed(UUID.randomUUID().toString())))))
                .from(0)
                .size(5)
                .build();
        SearchResponse<MovieData> response = elasticSearchRepository.search(request, MovieData.class);
        return convertMovie(response).stream()
                .sorted(Comparator.comparingInt(x -> x.getMovieName().length())).collect(Collectors.toList());
    }

    public List<MovieData> getMovieRecommendByRequest(String q) throws IOException {
        SearchRequest request = new SearchRequest.Builder()
                .index(MOVIES_RECOMMEND_ALIAS)
                .query(new Query.Builder()
                        .bool(new BoolQuery.Builder()
                                .should(new Query.Builder()
                                        .multiMatch(new MultiMatchQuery.Builder()
                                                .query(q)
                                                .fields("movieName", "movieName.keyword", "movieNameEnglish.keyword", "movieName.ngram", "movieNameEnglish", "movieNameEnglish.ngram")
                                                .build())
                                        .build())
                                .build())
                        .build()
                )
                .from(0)
                .size(5)
                .build();
        SearchResponse<MovieData> response = elasticSearchRepository.search(request, MovieData.class);
        return convertMovie(response).stream()
                .sorted(Comparator.comparingInt(x -> x.getMovieName().length())).collect(Collectors.toList());
    }

    private List<MovieData> convertMovie(SearchResponse<MovieData> response) {
        List<MovieData> res = new ArrayList<>();
        response.hits().hits().forEach(item -> res.add(item.source()));
        Gson gson = new Gson();
        String jsonString = gson.toJson(res);
        logger.info("elasticsearch res ::::: " + jsonString);
        return res;
    }
}
