package com.moviesfeed.models.persondetails;

import android.support.annotation.NonNull;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Pedro on 2017-04-24.
 */

@Entity
public class PersonCreditsScreen implements Comparable<PersonCreditsScreen> {

    @Id(autoincrement = true)
    private Long idDb;
    private int idTmdb;
    private String posterPath;
    private String knownFor;
    private String releaseDate;
    private boolean knownForCharacter;
    //milliseconds.
    private long releaseDateMs;

    private Long personCreditsKey;

    @Generated(hash = 1211074240)
    public PersonCreditsScreen(Long idDb, int idTmdb, String posterPath, String knownFor,
                               String releaseDate, boolean knownForCharacter, long releaseDateMs,
                               Long personCreditsKey) {
        this.idDb = idDb;
        this.idTmdb = idTmdb;
        this.posterPath = posterPath;
        this.knownFor = knownFor;
        this.releaseDate = releaseDate;
        this.knownForCharacter = knownForCharacter;
        this.releaseDateMs = releaseDateMs;
        this.personCreditsKey = personCreditsKey;
    }

    @Generated(hash = 1418012301)
    public PersonCreditsScreen() {
    }

    public Long getIdDb() {
        return this.idDb;
    }

    public void setIdDb(Long idDb) {
        this.idDb = idDb;
    }

    public int getIdTmdb() {
        return this.idTmdb;
    }

    public void setIdTmdb(int idTmdb) {
        this.idTmdb = idTmdb;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getKnownFor() {
        return this.knownFor;
    }

    public void setKnownFor(String knownFor) {
        this.knownFor = knownFor;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Long getPersonCreditsKey() {
        return this.personCreditsKey;
    }

    public void setPersonCreditsKey(Long personCreditsKey) {
        this.personCreditsKey = personCreditsKey;
    }

    public void setKnownForCharacter(boolean knownForCharacter) {
        this.knownForCharacter = knownForCharacter;
    }

    public void setReleaseDateMs(long releaseDateMs) {
        this.releaseDateMs = releaseDateMs;
    }

    public long getReleaseDateMs() {
        return releaseDateMs;
    }

    public boolean getKnownForCharacter() {
        return this.knownForCharacter;
    }

    @Override
    public int compareTo(@NonNull PersonCreditsScreen o) {
        if (getReleaseDateMs() > o.getReleaseDateMs())
            return -1;
        else if (getReleaseDateMs() == o.getReleaseDateMs())
            return 0;
        else
            return 1;
    }

}
