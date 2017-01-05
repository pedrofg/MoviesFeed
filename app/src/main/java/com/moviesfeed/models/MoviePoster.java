package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pedro on 8/28/2016.
 */
public class MoviePoster {

    @SerializedName("aspect_ratio")
    @Expose
    private double aspectRatio;
    @SerializedName("file_path")
    @Expose
    private String filePath;
    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("iso_639_1")
    @Expose
    private String iso6391;
    @SerializedName("vote_average")
    @Expose
    private double voteAverage;
    @SerializedName("vote_count")
    @Expose
    private int voteCount;
    @SerializedName("width")
    @Expose
    private int width;

    /**
     * @return The aspectRatio
     */
    public double getAspectRatio() {
        return aspectRatio;
    }

    /**
     * @param aspectRatio The aspect_ratio
     */
    public void setAspectRatio(double aspectRatio) {
        this.aspectRatio = aspectRatio;
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

    /**
     * @return The height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height The height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return The iso6391
     */
    public String getIso6391() {
        return iso6391;
    }

    /**
     * @param iso6391 The iso_639_1
     */
    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    /**
     * @return The voteAverage
     */
    public double getVoteAverage() {
        return voteAverage;
    }

    /**
     * @param voteAverage The vote_average
     */
    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    /**
     * @return The voteCount
     */
    public int getVoteCount() {
        return voteCount;
    }

    /**
     * @param voteCount The vote_count
     */
    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    /**
     * @return The width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width The width
     */
    public void setWidth(int width) {
        this.width = width;
    }

}
