package com.moviesfeed.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.moviesfeed.api.Filters;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class MoviesFeed {

    @Id
    private Long id;
    @SerializedName("page")
    @Expose
    private int page;
    @ToMany(referencedJoinProperty = "moviesFeedKey")
    @SerializedName("results")
    @Expose
    private List<Movie> movies;

    @Convert(converter = Filters.FiltersConverter.class, columnType = Long.class)
    private Filters filter;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1393672384)
    private transient MoviesFeedDao myDao;

    public MoviesFeed clone() {
        MoviesFeed newMoviesFeed = new MoviesFeed();
        newMoviesFeed.setId(this.id);
        newMoviesFeed.setPage(this.page);
        if (this.movies != null) {
            newMoviesFeed.setMovies(new ArrayList<Movie>());
            newMoviesFeed.getMovies().addAll(this.movies);
        }
        newMoviesFeed.setFilter(this.filter);
        return newMoviesFeed;
    }


    @Generated(hash = 306040833)
    public MoviesFeed() {
    }


    @Generated(hash = 1970418266)
    public MoviesFeed(Long id, int page, Filters filter) {
        this.id = id;
        this.page = page;
        this.filter = filter;
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }


    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 470865646)
    public synchronized void resetMovies() {
        movies = null;
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
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1292790097)
    public List<Movie> getMovies() {
        if (movies == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MovieDao targetDao = daoSession.getMovieDao();
            List<Movie> moviesNew = targetDao._queryMoviesFeed_Movies(id);
            synchronized (this) {
                if (movies == null) {
                    movies = moviesNew;
                }
            }
        }
        return movies;
    }


    public Filters getFilter() {
        return this.filter;
    }


    public void setFilterAndId(Filters filter) {
        this.filter = filter;
        this.id = filter.getId();
    }


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public void setFilter(Filters filter) {
        this.filter = filter;
    }


    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1783510636)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMoviesFeedDao() : null;
    }


}


