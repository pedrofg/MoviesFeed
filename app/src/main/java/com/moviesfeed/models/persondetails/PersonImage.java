package com.moviesfeed.models.persondetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Pedro on 2017-04-16.
 */
@Entity
public class PersonImage {

    @Id(autoincrement = true)
    private Long idDb;

    @SerializedName("aspect_ratio")
    @Expose
    private double aspectRatio;
    @SerializedName("file_path")
    @Expose
    private String filePath;
    @SerializedName("height")
    @Expose
    private long height;
    @SerializedName("vote_average")
    @Expose
    private double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private long voteCount;
    @SerializedName("width")
    @Expose
    private long width;

    private Long personImagesId;

    @Generated(hash = 1879477701)
    public PersonImage(Long idDb, double aspectRatio, String filePath, long height,
            double voteAverage, long voteCount, long width, Long personImagesId) {
        this.idDb = idDb;
        this.aspectRatio = aspectRatio;
        this.filePath = filePath;
        this.height = height;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.width = width;
        this.personImagesId = personImagesId;
    }

    @Generated(hash = 182820287)
    public PersonImage() {
    }

    public Long getIdDb() {
        return this.idDb;
    }

    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }

    public double getAspectRatio() {
        return this.aspectRatio;
    }

    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getHeight() {
        return this.height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public double getVoteAverage() {
        return this.voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public long getVoteCount() {
        return this.voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public long getWidth() {
        return this.width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public Long getPersonImagesId() {
        return this.personImagesId;
    }

    public void setPersonImagesId(Long personImagesId) {
        this.personImagesId = personImagesId;
    }
}
