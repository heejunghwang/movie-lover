package com.sample.movie.batch.jobs;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.sample.movie.batch.jobs.step.*;
import com.sample.movie.model.MovieData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Configuration
@RequiredArgsConstructor
public class MovieJob {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private String newIndexName;

    @Bean
    public Job indexingMovieJob() throws IOException {
        logger.info("indexingMovieJob has started");
        return jobBuilderFactory.get("indexingMovieJob")
                .start(configIndex())
                .next(indexMovieStep())
                .next(changeAliasStep())
                .build();
    }

    @Bean
    @JobScope
    public Step configIndex() {
        newIndexName = this.getNewIndexName();
        logger.info("configIndex has started");
        return stepBuilderFactory.get("configIndex")
                .tasklet(configMovieStep())
                .build();
    }

    @Bean
    @JobScope
    public Step indexMovieStep() throws IOException {
        logger.info("indexMovieStep has started");
        return stepBuilderFactory.get("movieStep")
                .<LinkedTreeMap, MovieData>chunk(50)
                .reader(readMovie())
                .processor(processMovie())
                .writer(writeMovie())
                .build();
    }

    @Bean
    @JobScope
    public Step changeAliasStep() {
        logger.info("changeAliasStep has started");
        return stepBuilderFactory.get("configIndex")
                .tasklet(addMovieAliasStep())
                .build();
    }

    @Bean
    @StepScope
    protected Tasklet configMovieStep() {
        return new MovieConfigProcessor(this.newIndexName);
    }

    @Bean
    @StepScope
    protected Tasklet addMovieAliasStep() {
        return new MovieAliasProcessor(this.newIndexName);
    }

    @Bean
    @StepScope
    public ItemWriter<MovieData> writeMovie() {
        return new MovieItemWriter(this.newIndexName);
    }

    @Bean
    @StepScope
    public MovieItemReader readMovie() throws IOException {
        logger.info("readMovie has started");

        String absolutePath = System.getProperty("user.dir");
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(absolutePath + "/src/main/resources/rawdata/searchMovieList_1.json"));

        LinkedTreeMap<?, ?> map = gson.fromJson(reader, LinkedTreeMap.class);
        LinkedTreeMap a = (LinkedTreeMap) map.get("movieListResult");
        List<LinkedTreeMap> b = (List<LinkedTreeMap>) a.get("movieList");

        return new MovieItemReader(b);
    }

    @Bean
    @StepScope
    public MovieTransformProcessor processMovie() {
        return new MovieTransformProcessor();
    }

    private String getNewIndexName() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        df.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String now = df.format(date);
        return "movies_" + now;
    }
}
