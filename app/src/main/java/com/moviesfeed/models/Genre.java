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
    private long movieDetailId;

    @Generated(hash = 2014791978)
    public Genre(Long id, String name, long movieDetailId) {
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
}
