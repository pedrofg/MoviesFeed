package com.moviesfeed.api;

import com.moviesfeed.models.MovieDetail;
import com.moviesfeed.models.MoviesFeed;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Pedro on 8/17/2016.
 */
public interface MoviesApi {

    String BASE_URL = "http://api.themoviedb.org/";
    String KEY = "";
    String URL_MOVIE_POSTER = "http://image.tmdb.org/t/p/w300/";
    String URL_MOVIE_BACKGROUND = "http://image.tmdb.org/t/p/w1000/";

    @GET("/3/discover/movie?api_key=" + KEY)
    Observable<MoviesFeed> getDiscoverMovie(@Query("sort_by") String sortBy, @Query("page") int page);

    @GET("/3/discover/movie?api_key=" + KEY + "&sort_by=primary_release_date.desc")
    Observable<MoviesFeed> getMovieByGenre(@Query("with_genres") int genre, @Query("primary_release_date.lte") String date, @Query("page") int page);

    @GET("/3/movie/{filter}?api_key=" + KEY)
    Observable<MoviesFeed> getMoviesFilterBy(@Path("filter") String filter, @Query("page") int page);


    @GET("/3/movie/{id}?api_key=" + KEY + "&append_to_response=images,videos")
    Observable<MovieDetail> getMovieDetail(@Path("id") int movieId);

}
