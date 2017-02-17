package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Pedro on 2017-02-16.
 */

@Entity
public class Review {

    @Id(autoincrement = true)
    private Long idDB;
    @SerializedName("id")
    @Expose
    private String idTmdb;
    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("url")
    @Expose
    private String url;
    private Long movieReviewId;
    @Generated(hash = 1720330061)
    public Review(Long idDB, String idTmdb, String author, String content,
            String url, Long movieReviewId) {
        this.idDB = idDB;
        this.idTmdb = idTmdb;
        this.author = author;
        this.content = content;
        this.url = url;
        this.movieReviewId = movieReviewId;
    }
    @Generated(hash = 2008964488)
    public Review() {
    }
    public Long getIdDB() {
        return this.idDB;
    }
    public void setIdDB(Long idDB) {
        this.idDB = idDB;
    }
    public String getIdTmdb() {
        return this.idTmdb;
    }
    public void setIdTmdb(String idTmdb) {
        this.idTmdb = idTmdb;
    }
    public String getAuthor() {
        return this.author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Long getMovieReviewId() {
        return this.movieReviewId;
    }
    public void setMovieReviewId(Long movieReviewId) {
        this.movieReviewId = movieReviewId;
    }
}
