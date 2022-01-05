package com.sample.movie.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.sample.movie.MovieLoverApplication;
import com.sample.movie.model.MovieData;
import com.sample.movie.repository.ElasticSearchRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {MovieLoverApplication.class})
@SpringBootTest(properties = "spring.config.location=classpath:/application-test.yaml")
public class MovieIndexServiceTest {
    @Autowired
    private MovieIndexService movieIndexService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String MOVIE_INDEX_TEST_NAME = "movie_index_test_" + UUID.randomUUID();

    private final String MOVIE_ALIAS_TEST_NAME = "movie_alias_test";

    @SpyBean
    ElasticSearchRepository elasticSearchRepository;

    @AfterEach()
    void deleteTextIndex() throws IOException {
        logger.info("delete index after test : " + MOVIE_INDEX_TEST_NAME);
        try {
            elasticSearchRepository.deleteIndex(MOVIE_INDEX_TEST_NAME);
        } catch (Exception e) {
            logger.info("there is no index");
        }
    }

    @Test()
    @DisplayName("영화 검색어 추천을 위한 인덱스를 설정 및 생성한다.")
    public void given_indexName_then_configIndex() throws IOException {
        // when
        movieIndexService.configIndex(MOVIE_INDEX_TEST_NAME);

        // then
        verify(elasticSearchRepository, times(1))
                .indexCreate(Mockito.any(CreateIndexRequest.class));
    }

    @Test()
    @DisplayName("영화 검색어 추천을 위한 인덱스를 설정 및 생성한다.")
    public void given_configureIndex_when_haveData_then_bulkInsert() throws IOException {
        // given
        movieIndexService.configIndex(MOVIE_INDEX_TEST_NAME);
        List<MovieData> givenDateList = new ArrayList<>();
        MovieData movieData = new MovieData();
        movieData.setMovieCode("00000");
        movieData.setMovieName("인기있는 영화");
        givenDateList.add(movieData);

        // when
        movieIndexService.bulkIndex(MOVIE_INDEX_TEST_NAME, givenDateList);

        // then
        verify(elasticSearchRepository, times(1))
                .bulkIndex(Mockito.any(BulkRequest.class));
    }

    @Test()
    @DisplayName("특정 alias 이름으로 걸려있는 index 리스트를 조회한다.")
    public void when_getIndexNameListOnAlias_then_returnIndexList() throws IOException {
        // when
        List<String> result = movieIndexService.getIndexNameListOnAlias(MOVIE_ALIAS_TEST_NAME);

        // then
        verify(elasticSearchRepository, times(1)).getAllAlias();
        Assertions.assertTrue(result != null && result.size() >= 0);
    }

    @Test()
    @DisplayName("특정 인덱스에 alias 를 지정한다.")
    public void given_thereIsIndex_when_addAlias_then_success() throws IOException {
        // given
        movieIndexService.configIndex(MOVIE_INDEX_TEST_NAME);

        // when
        movieIndexService.addAliasOnIndex(MOVIE_INDEX_TEST_NAME, MOVIE_ALIAS_TEST_NAME);

        // then
        verify(elasticSearchRepository, times(1)).addAlias(MOVIE_INDEX_TEST_NAME, MOVIE_ALIAS_TEST_NAME);
    }

    @Test()
    @DisplayName("인덱스가 있을 때 인덱스를 삭제한다.")
    public void given_thereIsIndex_when_deleteIndex_then_success() throws IOException {
        // given
        movieIndexService.configIndex(MOVIE_INDEX_TEST_NAME);

        // when
        List<String> deleteIndexList = new ArrayList<>();
        deleteIndexList.add(MOVIE_INDEX_TEST_NAME);
        movieIndexService.deleteIndex(deleteIndexList);

        // then
        verify(elasticSearchRepository, times(1)).deleteIndex(Mockito.any(String.class));
    }

}
