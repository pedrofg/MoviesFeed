package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Genre {

    @Id(autoincrement = true)
    private Long idDb;
    @SerializedName("id")
    @Expose
    private int idTmdb;
    @SerializedName("name")
    @Expose
    private String name;
    private Long movieDetailId;

    @Generated(hash = 415445634)
    public Genre(Long idDb, int idTmdb, String name, Long movieDetailId) {
        this.idDb = idDb;
        this.idTmdb = idTmdb;
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


    public void setMovieDetailId(Long movieDetailId) {
        this.movieDetailId = movieDetailId;
    }

    public int getIdTmdb() {
        return this.idTmdb;
    }

    public void setIdTmdb(int idTmdb) {
        this.idTmdb = idTmdb;
    }

    public Long getIdDb() {
        return this.idDb;
    }

    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }
}
