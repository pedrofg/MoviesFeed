package com.moviesfeed.models.persondetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.moviesfeed.models.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToOne;

/**
 * Created by Pedro on 2017-04-16.
 */

@Entity
public class Person {

    @SerializedName("biography")
    @Expose
    private String biography;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("deathday")
    @Expose
    private String deathday;
    @SerializedName("gender")
    @Expose
    private long gender;
    @SerializedName("homepage")
    @Expose
    private String homepage;
    @Id
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("place_of_birth")
    @Expose
    private String placeOfBirth;
    @SerializedName("popularity")
    @Expose
    private double popularity;
    @SerializedName("profile_path")
    @Expose
    private String imageProfilePath;
    @ToOne(joinProperty = "personMovieCreditsID")
    @SerializedName("movie_credits")
    @Expose
    private PersonMovieCredits personMovieCredits;
    @ToOne(joinProperty = "personImagesID")
    @SerializedName("images")
    @Expose
    private PersonImages personImages;

    private Long personMovieCreditsID;
    private Long personImagesID;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 778611619)
    private transient PersonDao myDao;

    @Generated(hash = 2100885405)
    public Person(String biography, String birthday, String deathday, long gender,
                  String homepage, Long id, String imdbId, String name,
                  String placeOfBirth, double popularity, String imageProfilePath,
                  Long personMovieCreditsID, Long personImagesID) {
        this.biography = biography;
        this.birthday = birthday;
        this.deathday = deathday;
        this.gender = gender;
        this.homepage = homepage;
        this.id = id;
        this.imdbId = imdbId;
        this.name = name;
        this.placeOfBirth = placeOfBirth;
        this.popularity = popularity;
        this.imageProfilePath = imageProfilePath;
        this.personMovieCreditsID = personMovieCreditsID;
        this.personImagesID = personImagesID;
    }

    @Generated(hash = 1024547259)
    public Person() {
    }

    public String getBiography() {
        return this.biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDeathday() {
        return this.deathday;
    }

    public void setDeathday(String deathday) {
        this.deathday = deathday;
    }

    public long getGender() {
        return this.gender;
    }

    public void setGender(long gender) {
        this.gender = gender;
    }

    public String getHomepage() {
        return this.homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImdbId() {
        return this.imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceOfBirth() {
        return this.placeOfBirth;
    }

    public void setPlaceOfBirth(String placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public double getPopularity() {
        return this.popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getImageProfilePath() {
        return this.imageProfilePath;
    }

    public void setImageProfilePath(String imageProfilePath) {
        this.imageProfilePath = imageProfilePath;
    }

    public Long getPersonMovieCreditsID() {
        return this.personMovieCreditsID;
    }

    public void setPersonMovieCreditsID(Long personMovieCreditsID) {
        this.personMovieCreditsID = personMovieCreditsID;
    }

    public Long getPersonImagesID() {
        return this.personImagesID;
    }

    public void setPersonImagesID(Long personImagesID) {
        this.personImagesID = personImagesID;
    }

    @Generated(hash = 1869425451)
    private transient Long personMovieCredits__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Keep
    @Generated(hash = 1473126998)
    public PersonMovieCredits getPersonMovieCredits() {
        Long __key = this.personMovieCreditsID;
        if (personMovieCredits == null && (personMovieCredits__resolvedKey == null
                || !personMovieCredits__resolvedKey.equals(__key))) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonMovieCreditsDao targetDao = daoSession.getPersonMovieCreditsDao();
            PersonMovieCredits personMovieCreditsNew = targetDao.load(__key);
            synchronized (this) {
                personMovieCredits = personMovieCreditsNew;
                personMovieCredits__resolvedKey = __key;
            }
        }
        return personMovieCredits;
    }


    @Generated(hash = 1027135073)
    private transient Long personImages__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Keep
    @Generated(hash = 303026490)
    public PersonImages getPersonImages() {
        Long __key = this.personImagesID;
        if (personImages == null && (personImages__resolvedKey == null || !personImages__resolvedKey.equals(__key))) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonImagesDao targetDao = daoSession.getPersonImagesDao();
            PersonImages personImagesNew = targetDao.load(__key);
            synchronized (this) {
                personImages = personImagesNew;
                personImages__resolvedKey = __key;
            }
        }
        return personImages;
    }


    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 2056799268)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPersonDao() : null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1516923215)
    public void setPersonMovieCredits(PersonMovieCredits personMovieCredits) {
        synchronized (this) {
            this.personMovieCredits = personMovieCredits;
            personMovieCreditsID = personMovieCredits == null ? null : personMovieCredits.getId();
            personMovieCredits__resolvedKey = personMovieCreditsID;
        }
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1342282865)
    public void setPersonImages(PersonImages personImages) {
        synchronized (this) {
            this.personImages = personImages;
            personImagesID = personImages == null ? null : personImages.getId();
            personImages__resolvedKey = personImagesID;
        }
    }


}
