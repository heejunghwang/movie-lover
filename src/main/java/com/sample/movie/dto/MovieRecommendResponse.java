package com.sample.movie.dto;

import com.sample.movie.model.MovieData;
import lombok.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MovieRecommendResponse {

    public MovieRecommendResponse() {
    }

    // 영화코드
    private String movieCode;

    // 영화명 (국문)
    private String movieName;

    // 영화명 (영문)
    private String movieNameEnglish;

    // 제작연도
    private Long productYear;

    // 개봉일
    private Long openDate;

    // 영화유형
    private String typeName;

    // 제작상태 (개봉예정, 개봉, 기타 등)
    private String productStateName;

    // 제작국가(전체)(한국, 미국 등. 국가명 공동 제작의 경우 ‘한국, 미국’)
    private String[] nations;

    // 영화장르(전체) (SF, 코미디 등 장르명. 복합 장르의 경우 ‘SF, 코미디’)
    private String[] genres;

    // 대표 제작국가
    private String representativeNationName;

    // 대표 장르명
    private String representativeGenreName;

    public List<MovieRecommendResponse> convertRecommendMovieList(List<MovieData> recommendMovieList){
        return recommendMovieList.stream()
                .sorted(Comparator.comparingInt(x -> x.getMovieName().length()))
                .map(item -> MovieRecommendResponse.builder()
                        .movieCode(item.getMovieCode())
                        .movieName(item.getMovieName())
                        .movieNameEnglish(item.getMovieNameEnglish())
                        .productYear(item.getProductYear())
                        .openDate(item.getOpenDate())
                        .typeName(item.getTypeName())
                        .productStateName(item.getProductStateName())
                        .nations(item.getNations())
                        .genres(item.getGenres())
                        .representativeNationName(item.getRepresentativeNationName())
                        .representativeGenreName(item.getRepresentativeGenreName())
                        .build()).collect(Collectors.toList());
    }

}
