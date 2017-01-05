package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Pedro on 8/24/2016.
 */
public class ProductionCountry {


    @SerializedName("iso_3166_1")
    @Expose
    private String initials;
    @SerializedName("name")
    @Expose
    private String name;

    /**
     * @return The initials
     */
    public String getInitials() {
        return initials;
    }

    /**
     * @param initials The iso_3166_1
     */
    public void setInitials(String initials) {
        this.initials = initials;
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
}
