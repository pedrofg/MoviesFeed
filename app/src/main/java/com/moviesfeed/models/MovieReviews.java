package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by Pedro on 2017-02-16.
 */

@Entity
public class MovieReviews {

    @Id
    private Long idDB;
    @SerializedName("id")
    @Expose
    private Long idTmdb;
    @SerializedName("page")
    @Expose
    private int page;
    @ToMany(referencedJoinProperty = "movieReviewId")
    @SerializedName("results")
    @Expose
    private List<Review> reviews;
    @SerializedName("total_pages")
    @Expose
    private int totalPages;
    @SerializedName("total_results")
    @Expose
    private int totalResults;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 979147886)
    private transient MovieReviewsDao myDao;

    @Generated(hash = 705869399)
    public MovieReviews(Long idDB, Long idTmdb, int page, int totalPages, int totalResults) {
        this.idDB = idDB;
        this.idTmdb = idTmdb;
        this.page = page;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    @Generated(hash = 1847980231)
    public MovieReviews() {
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return this.totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
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

    public Long getIdDB() {
        return this.idDB;
    }

    public void setIdDB(Long idDB) {
        this.idDB = idDB;
    }

    public Long getIdTmdb() {
        return this.idTmdb;
    }

    public void setIdTmdb(Long idTmdb) {
        this.idTmdb = idTmdb;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1357252894)
    public List<Review> getReviews() {
        if (reviews == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReviewDao targetDao = daoSession.getReviewDao();
            List<Review> reviewsNew = targetDao._queryMovieReviews_Reviews(idDB);
            synchronized (this) {
                if (reviews == null) {
                    reviews = reviewsNew;
                }
            }
        }
        return reviews;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 2133376601)
    public synchronized void resetReviews() {
        reviews = null;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1364655968)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMovieReviewsDao() : null;
    }
}
