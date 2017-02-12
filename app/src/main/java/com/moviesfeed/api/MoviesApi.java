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

    String FILTER_POPULARITY = "popular";
    String FILTER_UPCOMING = "upcoming";
    String FILTER_NOW_PLAYING = "now_playing";
    String FILTER_TOP_RATED = "top_rated";
    String FILTER_REVENUE = "revenue";
    String FILTER_SEARCH = "search";

    String GENRE_ACTION = "28";
    String GENRE_ANIMATION = "16";
    String GENRE_COMEDY = "35";
    String GENRE_ROMANCE = "10749";
    String GENRE_DRAMA = "18";
    String GENRE_SCIENCE_FICTION = "878";
    String GENRE_MUSIC = "10402";
    String GENRE_THRILLER = "53";
    String GENRE_HORROR = "27";
    String GENRE_DOCUMENTARY = "99";
    String GENRE_WAR = "10752";

    @GET("/3/discover/movie?api_key=" + KEY + "&include_adult=false")
    Observable<MoviesFeed> getDiscoverMovie(@Query("sort_by") String sortBy, @Query("page") int page);

    @GET("/3/discover/movie?api_key=" + KEY + "&sort_by=primary_release_date.desc&include_adult=false")
    Observable<MoviesFeed> getMovieByGenre(@Query("with_genres") int genre, @Query("primary_release_date.lte") String date, @Query("page") int page);

    @GET("/3/movie/{filter}?api_key=" + KEY)
    Observable<MoviesFeed> getMoviesFilterBy(@Path("filter") String filter, @Query("page") int page);

    @GET("/3/movie/{id}?api_key=" + KEY + "&append_to_response=images,videos,credits,similar")
    Observable<MovieDetail> getMovieDetail(@Path("id") int movieId);

    @GET("/3/search/movie?api_key=" + KEY + "&include_adult=false")
    Observable<MoviesFeed> getSearchMovie(@Query("query") String query, @Query("page") int page);

}
