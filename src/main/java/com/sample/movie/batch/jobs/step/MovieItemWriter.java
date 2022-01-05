package com.sample.movie.batch.jobs.step;

import com.sample.movie.model.MovieData;
import com.sample.movie.service.MovieIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

public class MovieItemWriter implements ItemWriter<MovieData> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MovieIndexService movieIndexService;

    private final String newIndexName;

    public MovieItemWriter(String newIndexName) {
        this.newIndexName = newIndexName;
    }

    @Override
    public void write(List<? extends MovieData> items) {
        this.movieIndexService.bulkIndex(newIndexName, items);
        logger.info("finished MovieItemWriter write");
    }
}
