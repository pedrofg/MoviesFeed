package com.moviesfeed.models.persondetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.moviesfeed.models.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

/**
 * Created by Pedro on 2017-04-16.
 */

@Entity
public class PersonMovieCredits {

    @Id
    private Long id;
    @ToMany(referencedJoinProperty = "personCreditsKey")
    @SerializedName("crew")
    @Expose
    private List<PersonCrew> personCrews;

    @ToMany(referencedJoinProperty = "personCreditsKey")
    @SerializedName("cast")
    @Expose
    private List<PersonCast> personCasts;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 739692311)
    private transient PersonMovieCreditsDao myDao;


    @Generated(hash = 1456470338)
    public PersonMovieCredits() {
    }


    @Generated(hash = 520206377)
    public PersonMovieCredits(Long id) {
        this.id = id;
    }


    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1591145529)
    public synchronized void resetPersonCrews() {
        personCrews = null;
    }


    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 199082770)
    public synchronized void resetPersonCasts() {
        personCasts = null;
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
    @Generated(hash = 270117347)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPersonMovieCreditsDao() : null;
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
    @Generated(hash = 438171302)
    public List<PersonCrew> getPersonCrews() {
        if (personCrews == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonCrewDao targetDao = daoSession.getPersonCrewDao();
            List<PersonCrew> personCrewsNew = targetDao
                    ._queryPersonMovieCredits_PersonCrews(id);
            synchronized (this) {
                if (personCrews == null) {
                    personCrews = personCrewsNew;
                }
            }
        }
        return personCrews;
    }

    public void setPersonCrews(List<PersonCrew> personCrews) {
        this.personCrews = personCrews;
    }

    public void setPersonCasts(List<PersonCast> personCasts) {
        this.personCasts = personCasts;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 888858030)
    public List<PersonCast> getPersonCasts() {
        if (personCasts == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonCastDao targetDao = daoSession.getPersonCastDao();
            List<PersonCast> personCastsNew = targetDao
                    ._queryPersonMovieCredits_PersonCasts(id);
            synchronized (this) {
                if (personCasts == null) {
                    personCasts = personCastsNew;
                }
            }
        }
        return personCasts;
    }

}
