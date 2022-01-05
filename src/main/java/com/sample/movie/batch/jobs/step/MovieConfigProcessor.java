package com.sample.movie.batch.jobs.step;

import com.sample.movie.service.MovieIndexService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;


public class MovieConfigProcessor implements Tasklet {

    @Autowired
    private MovieIndexService movieIndexService;

    private String newIndexName;

    public MovieConfigProcessor(String newIndexName) {
        this.newIndexName = newIndexName;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution,
                                ChunkContext chunkContext) throws Exception {
        this.movieIndexService.configIndex(newIndexName);
        return RepeatStatus.FINISHED;
    }
}