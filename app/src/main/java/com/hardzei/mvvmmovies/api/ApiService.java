package com.hardzei.mvvmmovies.api;

import com.hardzei.mvvmmovies.pojo.MovieResponse;
import com.hardzei.mvvmmovies.pojo.ReviewResponse;
import com.hardzei.mvvmmovies.pojo.TrailerResponse;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("discover/movie")
    Observable<MovieResponse> getMovies(@Query("api_key") String apiKey,
                                        @Query("language") String language,
                                        @Query("sort_by") String sort_by,
                                        @Query("vote_count.gte") int vote_count,
                                        @Query("page") int page);

    @GET("movie/{id}/reviews")
    Observable<ReviewResponse> getReviews(@Path("id") int id,
                                          @Query("api_key") String apiKey,
                                          @Query("language") String language);

    @GET("movie/{id}/videos")
    Observable<TrailerResponse> getTrailers(@Path("id") int id,
                                            @Query("api_key") String apiKey,
                                            @Query("language") String language);
}
