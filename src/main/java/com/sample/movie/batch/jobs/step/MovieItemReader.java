package com.sample.movie.batch.jobs.step;

import com.google.gson.internal.LinkedTreeMap;
import org.springframework.batch.item.ItemReader;

import java.util.List;

public class MovieItemReader implements ItemReader<LinkedTreeMap> {

    private int nextMovieIndex;
    private List<LinkedTreeMap> movieList;

    public MovieItemReader(List<LinkedTreeMap> rawMovies) {
        initialize(rawMovies);
    }

    private void initialize(List<LinkedTreeMap> rawMovies) {
        movieList = rawMovies;
        nextMovieIndex = 0;
    }

    @Override
    public LinkedTreeMap read() {
        LinkedTreeMap nextMovie = null;
        if (nextMovieIndex < movieList.size()) {
            nextMovie = movieList.get(nextMovieIndex);
            nextMovieIndex++;
        } else {
            nextMovieIndex = 0;
        }
        return nextMovie;
    }
}