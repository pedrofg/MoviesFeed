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
public class PersonImages {

    @Id
    private Long id;
    @ToMany(referencedJoinProperty = "personImagesId")
    @SerializedName("profiles")
    @Expose
    private List<PersonImage> personImageList;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1058680089)
    private transient PersonImagesDao myDao;


    @Generated(hash = 531461917)
    public PersonImages() {
    }


    @Generated(hash = 1133204387)
    public PersonImages(Long id) {
        this.id = id;
    }


    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1943019818)
    public synchronized void resetPersonImageList() {
        personImageList = null;
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
    @Generated(hash = 275810949)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPersonImagesDao() : null;
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
    @Generated(hash = 1321178443)
    public List<PersonImage> getPersonImageList() {
        if (personImageList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PersonImageDao targetDao = daoSession.getPersonImageDao();
            List<PersonImage> personImageListNew = targetDao
                    ._queryPersonImages_PersonImageList(id);
            synchronized (this) {
                if (personImageList == null) {
                    personImageList = personImageListNew;
                }
            }
        }
        return personImageList;
    }

}
