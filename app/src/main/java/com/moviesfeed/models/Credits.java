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
 * Created by Pedro on 2017-01-28.
 */
@Entity
public class Credits {

    @Id(autoincrement = true)
    private Long idDb;
    @ToMany(referencedJoinProperty = "creditsKey")
    @SerializedName("cast")
    @Expose
    private List<Cast> cast;
    @ToMany(referencedJoinProperty = "creditsKey")
    @SerializedName("crew")
    @Expose
    private List<Crew> crew;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1476102523)
    private transient CreditsDao myDao;
    @Generated(hash = 1594016963)
    public Credits(Long idDb) {
        this.idDb = idDb;
    }
    @Generated(hash = 688025143)
    public Credits() {
    }
    public Long getIdDb() {
        return this.idDb;
    }
    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 698744509)
    public List<Cast> getCast() {
        if (cast == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CastDao targetDao = daoSession.getCastDao();
            List<Cast> castNew = targetDao._queryCredits_Cast(idDb);
            synchronized (this) {
                if (cast == null) {
                    cast = castNew;
                }
            }
        }
        return cast;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 711953200)
    public synchronized void resetCast() {
        cast = null;
    }
    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 104151537)
    public List<Crew> getCrew() {
        if (crew == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            CrewDao targetDao = daoSession.getCrewDao();
            List<Crew> crewNew = targetDao._queryCredits_Crew(idDb);
            synchronized (this) {
                if (crew == null) {
                    crew = crewNew;
                }
            }
        }
        return crew;
    }
    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 901153372)
    public synchronized void resetCrew() {
        crew = null;
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
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 666285910)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getCreditsDao() : null;
    }

}
