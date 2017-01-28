package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Pedro on 2017-01-28.
 */
@Entity
public class Cast {

    @Id(autoincrement = true)
    private Long idDb;
    @SerializedName("cast_id")
    @Expose
    private int castId;
    @SerializedName("character")
    @Expose
    private String character;
    @SerializedName("credit_id")
    @Expose
    private String creditId;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("order")
    @Expose
    private int order;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;
    private Long creditsKey;
    @Generated(hash = 2052370037)
    public Cast(Long idDb, int castId, String character, String creditId, int id,
            String name, int order, String profilePath, Long creditsKey) {
        this.idDb = idDb;
        this.castId = castId;
        this.character = character;
        this.creditId = creditId;
        this.id = id;
        this.name = name;
        this.order = order;
        this.profilePath = profilePath;
        this.creditsKey = creditsKey;
    }
    @Generated(hash = 971498007)
    public Cast() {
    }
    public Long getIdDb() {
        return this.idDb;
    }
    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }
    public int getCastId() {
        return this.castId;
    }
    public void setCastId(int castId) {
        this.castId = castId;
    }
    public String getCharacter() {
        return this.character;
    }
    public void setCharacter(String character) {
        this.character = character;
    }
    public String getCreditId() {
        return this.creditId;
    }
    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getOrder() {
        return this.order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public String getProfilePath() {
        return this.profilePath;
    }
    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
    public Long getCreditsKey() {
        return this.creditsKey;
    }
    public void setCreditsKey(Long creditsKey) {
        this.creditsKey = creditsKey;
    }


}
