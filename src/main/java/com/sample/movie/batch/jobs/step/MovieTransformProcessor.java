package com.sample.movie.batch.jobs.step;

import com.google.gson.internal.LinkedTreeMap;
import com.sample.movie.model.MovieData;
import org.springframework.batch.item.ItemProcessor;

import java.util.Random;

public class MovieTransformProcessor implements ItemProcessor<LinkedTreeMap, MovieData> {

    @Override
    public MovieData process(LinkedTreeMap rawMovie) {

        MovieData movie = new MovieData();
        if (rawMovie != null && !rawMovie.isEmpty()) {
            if (rawMovie.get("movieCd") != null) {
                movie.setMovieCode((String) rawMovie.get("movieCd"));
            }
            if (rawMovie.get("movieNm") != null) {
                movie.setMovieName(((String) rawMovie.get("movieNm")).trim());
            }
            if (rawMovie.get("movieNmEn") != null) {
                movie.setMovieNameEnglish(((String) rawMovie.get("movieNmEn")).trim());
            }
            if (rawMovie.get("prdtYear") != null) {
                String productYearString = (String) rawMovie.get("prdtYear");
                movie.setProductYear(!productYearString.isEmpty() ? Long.parseLong((String) rawMovie.get("prdtYear")) : null);
            }
            if (rawMovie.get("openDt") != null) {
                String openDateString = (String) rawMovie.get("openDt");
                movie.setOpenDate(!openDateString.isEmpty() ? Long.parseLong((String) rawMovie.get("openDt")) : null);
            }
            if (rawMovie.get("typeNm") != null) {
                movie.setTypeName((String) rawMovie.get("typeNm"));
            }
            if (rawMovie.get("prdtStatNm") != null) {
                movie.setProductStateName((String) rawMovie.get("prdtStatNm"));
            }

            String nationsString = (String) rawMovie.get("nationAlt");
            if (nationsString != null && !nationsString.isEmpty()) {
                String[] nationsStringArray = nationsString.split("[,/]");
                movie.setNations(nationsStringArray);
            }

            String genres = (String) rawMovie.get("genreAlt");
            if (genres != null && !genres.equals("")) {
                String[] genreArray = genres.split("[,/]");
                movie.setGenres(genreArray);
            }

            if (rawMovie.get("repNationNm") != null) {
                movie.setRepresentativeNationName((String) rawMovie.get("repNationNm"));
            }
            if (rawMovie.get("repGenreNm") != null) {
                movie.setRepresentativeGenreName((String) rawMovie.get("repGenreNm"));
            }
            movie.setViewCount(createRandomViewCount());
            return movie;
        } else {
            return null;
        }
    }

    private long createRandomViewCount(){
        Random r = new Random();
        int low = 0;
        int high = Integer.MAX_VALUE - 100;
        return Long.parseLong(String.valueOf(r.nextInt(high-low) + low));
    }
}
