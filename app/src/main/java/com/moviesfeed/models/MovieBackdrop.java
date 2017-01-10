package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class MovieBackdrop {

    @Id(autoincrement = true)
    private Long id;
    @SerializedName("file_path")
    @Expose
    private String filePath;
    private long movieDetailId;

    @Generated(hash = 1056359085)
    public MovieBackdrop(Long id, String filePath, long movieDetailId) {
        this.id = id;
        this.filePath = filePath;
        this.movieDetailId = movieDetailId;
    }

    @Generated(hash = 20645344)
    public MovieBackdrop() {
    }

    /**
     * @return The filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath The file_path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }




    public long getMovieDetailId() {
        return movieDetailId;
    }

    public void setMovieDetailId(long movieDetailId) {
        this.movieDetailId = movieDetailId;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
