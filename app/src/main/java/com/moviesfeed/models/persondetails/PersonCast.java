package com.moviesfeed.models.persondetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Pedro on 2017-04-16.
 */

@Entity
public class PersonCast {

    @Id(autoincrement = true)
    private Long idDb;

    @SerializedName("adult")
    @Expose
    private boolean adult;
    @SerializedName("character")
    @Expose
    private String character;
    @SerializedName("credit_id")
    @Expose
    private String creditId;
    @SerializedName("id")
    @Expose
    private int idTmdb;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("title")
    @Expose
    private String title;

    private Long personCreditsKey;

    @Generated(hash = 1354492562)
    public PersonCast() {
    }

    @Generated(hash = 529143495)
    public PersonCast(Long idDb, boolean adult, String character, String creditId,
            int idTmdb, String originalTitle, String posterPath, String releaseDate,
            String title, Long personCreditsKey) {
        this.idDb = idDb;
        this.adult = adult;
        this.character = character;
        this.creditId = creditId;
        this.idTmdb = idTmdb;
        this.originalTitle = originalTitle;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.title = title;
        this.personCreditsKey = personCreditsKey;
    }

    public void setIdTmdb(int idTmdb) {
        this.idTmdb = idTmdb;
    }

    public int getIdTmdb() {
        return idTmdb;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }


    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getIdDb() {
        return this.idDb;
    }

    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }

    public boolean getAdult() {
        return this.adult;
    }

    public Long getPersonCreditsKey() {
        return this.personCreditsKey;
    }

    public void setPersonCreditsKey(Long personCreditsKey) {
        this.personCreditsKey = personCreditsKey;
    }
}
