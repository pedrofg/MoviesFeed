package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Pedro on 1/22/2017.
 */

@Entity
public class Video {


    @Id
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("key")
    @Expose
    private String key;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("site")
    @Expose
    private String site;
    @SerializedName("size")
    @Expose
    private int size;
    @SerializedName("type")
    @Expose
    private String type;
    private Long movieVideosId;
    public static final String YOUTUBE_SITE = "YouTube";
    public static final String REPLACE_KEY = "{$}";
    public static final String YOUTUBE_THUMBNAIL_URL = "http://img.youtube.com/vi/" + REPLACE_KEY + "/0.jpg";
    public static final String YOUTUBE_URL = "http://www.youtube.com/watch?v=";

    @Generated(hash = 1986016537)
    public Video(String id, String key, String name, String site, int size,
                 String type, Long movieVideosId) {
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
        this.movieVideosId = movieVideosId;
    }

    @Generated(hash = 237528154)
    public Video() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return this.site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getMovieVideosId() {
        return this.movieVideosId;
    }

    public void setMovieVideosId(Long movieVideosId) {
        this.movieVideosId = movieVideosId;
    }

    public String getYoutubeThumbnailUrl() {
        return YOUTUBE_THUMBNAIL_URL.replace(REPLACE_KEY, this.key);
    }

    public String getYoutubeUrl() {
        return YOUTUBE_URL + this.key;
    }

}
