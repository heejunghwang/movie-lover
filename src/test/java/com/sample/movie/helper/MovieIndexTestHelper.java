package com.sample.movie.helper;

import com.sample.movie.service.MovieIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import static com.sample.movie.service.MovieRecommendService.MOVIES_RECOMMEND_ALIAS;

@Component
public class MovieIndexTestHelper {

    @Autowired
    private MovieIndexService movieIndexService;

    private final String TEST_INDEX_NAME = "test_index";

    public void setUp() throws IOException {
        movieIndexService.configIndex(TEST_INDEX_NAME);
        movieIndexService.addAliasOnIndex(TEST_INDEX_NAME, MOVIES_RECOMMEND_ALIAS);
    }

}
