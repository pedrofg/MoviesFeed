package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Movie {

    //idDb was created to ensure the same movies order from the server.
    @Id(autoincrement = true)
    private Long idDb;
    @SerializedName("id")
    @Expose
    private int idTmdb;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("vote_average")
    @Expose
    private double voteAverage;
    private Long moviesFeedKey;
    private Long similarMovieKey;


    @Generated(hash = 1263461133)
    public Movie() {
    }


    @Generated(hash = 992495951)
    public Movie(Long idDb, int idTmdb, String posterPath, String originalTitle,
            String title, String overview, String backdropPath, double voteAverage,
            Long moviesFeedKey, Long similarMovieKey) {
        this.idDb = idDb;
        this.idTmdb = idTmdb;
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.title = title;
        this.overview = overview;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.moviesFeedKey = moviesFeedKey;
        this.similarMovieKey = similarMovieKey;
    }


    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }


    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getMoviesFeedKey() {
        return this.moviesFeedKey;
    }

    public int getIdTmdb() {
        return idTmdb;
    }


    public Long getIdDb() {
        return this.idDb;
    }


    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }


    public void setIdTmdb(int idTmdb) {
        this.idTmdb = idTmdb;
    }


    public void setMoviesFeedKey(Long moviesFeedKey) {
        this.moviesFeedKey = moviesFeedKey;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }


    public double getVoteAverage() {
        return this.voteAverage;
    }


    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }


    public Long getSimilarMovieKey() {
        return this.similarMovieKey;
    }


    public void setSimilarMovieKey(Long similarMovieKey) {
        this.similarMovieKey = similarMovieKey;
    }
}
