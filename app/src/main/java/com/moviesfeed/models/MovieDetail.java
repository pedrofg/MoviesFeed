package com.moviesfeed.models;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;
import java.util.List;

@Entity
public class MovieDetail {

    @SerializedName("budget")
    @Expose
    private int budget;
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
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("revenue")
    @Expose
    private long revenue;
    @SerializedName("runtime")
    @Expose
    private int runtime;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("vote_average")
    @Expose
    private double voteAverage;
    @ToMany(referencedJoinProperty = "movieDetailId")
    @SerializedName("backdrops")
    @Expose
    private List<MovieBackdrop> backdrops;
    @ToMany(referencedJoinProperty = "movieDetailId")
    @SerializedName("genres")
    @Expose
    private List<Genre> genres;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 275808995)
    private transient MovieDetailDao myDao;

    @Generated(hash = 1391283944)
    public MovieDetail(int budget, String homepage, Long id, String imdbId, String overview,
                       String posterPath, String releaseDate, long revenue, int runtime, String title,
                       double voteAverage) {
        this.budget = budget;
        this.homepage = homepage;
        this.id = id;
        this.imdbId = imdbId;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.revenue = revenue;
        this.runtime = runtime;
        this.title = title;
        this.voteAverage = voteAverage;
    }

    @Generated(hash = 850277392)
    public MovieDetail() {
    }

    /**
     * @return The budget
     */
    public int getBudget() {
        return budget;
    }

    /**
     * @param budget The budget
     */
    public void setBudget(int budget) {
        this.budget = budget;
    }


    /**
     * @return The homepage
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * @param homepage The homepage
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }


    /**
     * @return The imdbId
     */
    public String getImdbId() {
        return imdbId;
    }

    /**
     * @param imdbId The imdb_id
     */
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }


    /**
     * @return The overview
     */
    public String getOverview() {
        return overview;
    }

    /**
     * @param overview The overview
     */
    public void setOverview(String overview) {
        this.overview = overview;
    }


    /**
     * @return The posterPath
     */
    public String getPosterPath() {
        return posterPath;
    }

    /**
     * @param posterPath The poster_path
     */
    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }


    /**
     * @return The releaseDate
     */
    public String getReleaseDate() {
        return releaseDate;
    }

    /**
     * @param releaseDate The release_date
     */
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    /**
     * @return The revenue
     */
    public long getRevenue() {
        return revenue;
    }

    /**
     * @param revenue The revenue
     */
    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    /**
     * @return The runtime
     */
    public int getRuntime() {
        return runtime;
    }

    /**
     * @param runtime The runtime
     */
    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }


    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
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
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 2089719922)
    public List<MovieBackdrop> getBackdrops() {
        if (backdrops == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MovieBackdropDao targetDao = daoSession.getMovieBackdropDao();
            List<MovieBackdrop> backdropsNew = targetDao
                    ._queryMovieDetail_Backdrops(id);
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
    @Generated(hash = 116324377)
    public List<Genre> getGenres() {
        if (genres == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GenreDao targetDao = daoSession.getGenreDao();
            List<Genre> genresNew = targetDao._queryMovieDetail_Genres(id);
            synchronized (this) {
                if (genres == null) {
                    genres = genresNew;
                }
            }
        }
        return genres;
    }

    public void setBackdrops(List<MovieBackdrop> backdrops) {
        this.backdrops = backdrops;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1988821389)
    public synchronized void resetGenres() {
        genres = null;
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

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1814069270)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMovieDetailDao() : null;
    }

}
