package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Genre {

    @Id(autoincrement = true)
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("name")
    @Expose
    private String name;
    private Long movieDetailId;

    @Generated(hash = 1329650085)
    public Genre(Long id, String name, Long movieDetailId) {
        this.id = id;
        this.name = name;
        this.movieDetailId = movieDetailId;
    }

    @Generated(hash = 235763487)
    public Genre() {
    }


    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public long getMovieDetailId() {
        return movieDetailId;
    }

    public void setMovieDetailId(long movieDetailId) {
        this.movieDetailId = movieDetailId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public void setMovieDetailId(Long movieDetailId) {
        this.movieDetailId = movieDetailId;
    }
}
