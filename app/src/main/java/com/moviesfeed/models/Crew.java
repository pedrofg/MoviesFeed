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
public class Crew {

    @Id(autoincrement = true)
    private Long idDb;
    @SerializedName("credit_id")
    @Expose
    private String creditId;
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("job")
    @Expose
    private String job;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("profile_path")
    @Expose
    private String profilePath;
    private Long creditsKey;
    @Generated(hash = 1901458944)
    public Crew(Long idDb, String creditId, String department, int id, String job,
            String name, String profilePath, Long creditsKey) {
        this.idDb = idDb;
        this.creditId = creditId;
        this.department = department;
        this.id = id;
        this.job = job;
        this.name = name;
        this.profilePath = profilePath;
        this.creditsKey = creditsKey;
    }
    @Generated(hash = 262773304)
    public Crew() {
    }
    public Long getIdDb() {
        return this.idDb;
    }
    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }
    public String getCreditId() {
        return this.creditId;
    }
    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }
    public String getDepartment() {
        return this.department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getJob() {
        return this.job;
    }
    public void setJob(String job) {
        this.job = job;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
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
