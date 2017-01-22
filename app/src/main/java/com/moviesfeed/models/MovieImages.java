package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by Pedro on 1/21/2017.
 */

@Entity
public class MovieImages {

    @Id
    private Long id;
    @ToMany(referencedJoinProperty = "movieImagesId")
    @SerializedName("backdrops")
    @Expose
    private List<MovieBackdrop> backdrops = null;
    @ToMany(referencedJoinProperty = "movieImagesId")
    @SerializedName("posters")
    @Expose
    private List<MoviePoster> posters = null;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 568685165)
    private transient MovieImagesDao myDao;

    @Generated(hash = 1564495695)
    public MovieImages(Long id) {
        this.id = id;
    }

    @Generated(hash = 570987842)
    public MovieImages() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 167113787)
    public List<MovieBackdrop> getBackdrops() {
        if (backdrops == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MovieBackdropDao targetDao = daoSession.getMovieBackdropDao();
            List<MovieBackdrop> backdropsNew = targetDao
                    ._queryMovieImages_Backdrops(id);
            synchronized (this) {
                if (backdrops == null) {
                    backdrops = backdropsNew;
                }
            }
        }
        return backdrops;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 2027946155)
    public synchronized void resetBackdrops() {
        backdrops = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1206802029)
    public List<MoviePoster> getPosters() {
        if (posters == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MoviePosterDao targetDao = daoSession.getMoviePosterDao();
            List<MoviePoster> postersNew = targetDao._queryMovieImages_Posters(id);
            synchronized (this) {
                if (posters == null) {
                    posters = postersNew;
                }
            }
        }
        return posters;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 675098208)
    public synchronized void resetPosters() {
        posters = null;
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

    public void setBackdrops(List<MovieBackdrop> backdrops) {
        this.backdrops = backdrops;
    }

    public void setPosters(List<MoviePoster> posters) {
        this.posters = posters;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1120473224)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMovieImagesDao() : null;
    }
}
