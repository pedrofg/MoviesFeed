package com.moviesfeed.models;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;

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
    @SerializedName("genres")
    @Expose
    private List<Genre> genres;
    @ToOne(joinProperty = "imagesId")
    @SerializedName("images")
    @Expose
    private MovieImages images;
    @ToOne(joinProperty = "videosId")
    @SerializedName("videos")
    @Expose
    private MovieVideos videos;
    @ToOne(joinProperty = "creditsId")
    @SerializedName("credits")
    @Expose
    private Credits credits;
    @ToOne(joinProperty = "similarId")
    @SerializedName("similar")
    @Expose
    private SimilarMovies similarMovies;


    private Long imagesId;
    private Long videosId;
    private Long creditsId;
    private Long similarId;

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
    @Generated(hash = 1730634245)
    private transient Long images__resolvedKey;
    @Generated(hash = 458023817)
    private transient Long videos__resolvedKey;
    @Generated(hash = 2130436260)
    private transient Long credits__resolvedKey;
    @Generated(hash = 1068834957)
    private transient Long similarMovies__resolvedKey;

    @Generated(hash = 981060198)
    public MovieDetail(int budget, String homepage, Long id, String imdbId, String overview,
                       String posterPath, String releaseDate, long revenue, int runtime, String title,
                       double voteAverage, Long imagesId, Long videosId, Long creditsId, Long similarId) {
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
        this.imagesId = imagesId;
        this.videosId = videosId;
        this.creditsId = creditsId;
        this.similarId = similarId;
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

    public Long getImagesId() {
        return this.imagesId;
    }

    public void setImagesId(Long imagesId) {
        this.imagesId = imagesId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Keep
    @Generated(hash = 1057636331)
    public MovieImages getImages() {
        Long __key = this.imagesId;
        if (images == null && (images__resolvedKey == null || !images__resolvedKey.equals(__key))) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MovieImagesDao targetDao = daoSession.getMovieImagesDao();
            MovieImages imagesNew = targetDao.load(__key);
            synchronized (this) {
                images = imagesNew;
                images__resolvedKey = __key;
            }
        }
        return images;
    }


    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1372965984)
    public void setImages(MovieImages images) {
        synchronized (this) {
            this.images = images;
            imagesId = images == null ? null : images.getId();
            images__resolvedKey = imagesId;
        }
    }

    public Long getVideosId() {
        return this.videosId;
    }

    public void setVideosId(Long videosId) {
        this.videosId = videosId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Keep
    @Generated(hash = 729026238)
    public MovieVideos getVideos() {
        Long __key = this.videosId;
        if (videos == null && (videos__resolvedKey == null || !videos__resolvedKey.equals(__key))) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MovieVideosDao targetDao = daoSession.getMovieVideosDao();
            MovieVideos videosNew = targetDao.load(__key);
            synchronized (this) {
                videos = videosNew;
                videos__resolvedKey = __key;
            }
        }
        return videos;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 397838886)
    public void setVideos(MovieVideos videos) {
        synchronized (this) {
            this.videos = videos;
            videosId = videos == null ? null : videos.getId();
            videos__resolvedKey = videosId;
        }
    }

    public Long getCreditsId() {
        return this.creditsId;
    }

    public void setCreditsId(Long creditsId) {
        this.creditsId = creditsId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Keep
    @Generated(hash = 822197607)
    public Credits getCredits() {
        Long __key = this.creditsId;
        if (credits == null && (credits__resolvedKey == null || !credits__resolvedKey.equals(__key))) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CreditsDao targetDao = daoSession.getCreditsDao();
            Credits creditsNew = targetDao.load(__key);
            synchronized (this) {
                credits = creditsNew;
                credits__resolvedKey = __key;
            }
        }
        return credits;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1676176909)
    public void setCredits(Credits credits) {
        synchronized (this) {
            this.credits = credits;
            creditsId = credits == null ? null : credits.getIdDb();
            credits__resolvedKey = creditsId;
        }
    }

    public Long getSimilarId() {
        return this.similarId;
    }

    public void setSimilarId(Long similarId) {
        this.similarId = similarId;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Keep
    @Generated(hash = 1470876945)
    public SimilarMovies getSimilarMovies() {
        Long __key = this.similarId;
        if (similarMovies == null && (similarMovies__resolvedKey == null || !similarMovies__resolvedKey.equals(__key))) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SimilarMoviesDao targetDao = daoSession.getSimilarMoviesDao();
            SimilarMovies similarMoviesNew = targetDao.load(__key);
            synchronized (this) {
                similarMovies = similarMoviesNew;
                similarMovies__resolvedKey = __key;
            }
        }
        return similarMovies;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1880056691)
    public void setSimilarMovies(SimilarMovies similarMovies) {
        synchronized (this) {
            this.similarMovies = similarMovies;
            similarId = similarMovies == null ? null : similarMovies.getId();
            similarMovies__resolvedKey = similarId;
        }
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1814069270)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMovieDetailDao() : null;
    }

}
