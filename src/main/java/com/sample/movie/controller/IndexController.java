package com.sample.movie.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "index")
public class IndexController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("indexingMovieJob")
    private Job job;

    @ApiOperation(value = "영화 데이터를 색인한다.")
    @PostMapping("movies-recommend")
    public String indexMovies(String... args) throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .toJobParameters();
        jobLauncher.run(job, params);
        return "OK";
    }
}
