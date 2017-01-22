package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Pedro on 1/21/2017.
 */

@Entity
public class MoviePoster {

    @Id(autoincrement = true)
    private Long id;
    @SerializedName("file_path")
    @Expose
    private String filePath;
    private Long movieImagesId;
    @Generated(hash = 1414992469)
    public MoviePoster(Long id, String filePath, Long movieImagesId) {
        this.id = id;
        this.filePath = filePath;
        this.movieImagesId = movieImagesId;
    }
    @Generated(hash = 547620416)
    public MoviePoster() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFilePath() {
        return this.filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public Long getMovieImagesId() {
        return this.movieImagesId;
    }
    public void setMovieImagesId(Long movieImagesId) {
        this.movieImagesId = movieImagesId;
    }



}
