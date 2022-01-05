package com.sample.movie.batch.jobs.step;

import com.sample.movie.service.MovieIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MovieAliasProcessor implements Tasklet {

    @Autowired
    private MovieIndexService movieIndexService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String newIndexName;

    private final String MOVIE_ALIAS = "movies-recommend";

    public MovieAliasProcessor(String newIndexName) {
        this.newIndexName = newIndexName;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        List<String> oldIndexList = this.movieIndexService.getIndexNameListOnAlias(MOVIE_ALIAS);
        this.movieIndexService.addAliasOnIndex(newIndexName, MOVIE_ALIAS);
        if (oldIndexList != null) {
            if (oldIndexList.size() != 0) {
                // 이전 인덱스들 삭제
                if (oldIndexList.size() != 0) {
                    this.movieIndexService.deleteIndex(oldIndexList);
                }
            }
        }
        logger.info("executed MovieAliasProcessor");
        return null;
    }

}

