package com.moviesfeed.api;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Pedro on 8/17/2016.
 */
public enum Filters {
    POPULARITY(0), UPCOMING(1), NOW_PLAYING(2), TOP_RATED(3), REVENUE(4),
    ACTION(5), ANIMATION(6), COMEDY(7), ROMANCE(8), DRAMA(9), SCIENCE_FICTION(10),
    MUSIC(11), THRILLER(12), HORROR(13), DOCUMENTARY(14), WAR(15), SEARCH(16);

    private final long id;

    Filters(int id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        String name = "";
        if (id == POPULARITY.getId()) {
            name = MoviesApi.FILTER_POPULARITY;
        } else if (id == UPCOMING.getId()) {
            name = MoviesApi.FILTER_UPCOMING;
        } else if (id == NOW_PLAYING.getId()) {
            name = MoviesApi.FILTER_NOW_PLAYING;
        } else if (id == TOP_RATED.getId()) {
            name = MoviesApi.FILTER_TOP_RATED;
        } else if (id == REVENUE.getId()) {
            name = MoviesApi.FILTER_REVENUE;
        } else if (id == ACTION.getId()) {
            name = MoviesApi.GENRE_ACTION;
        } else if (id == ANIMATION.getId()) {
            name = MoviesApi.GENRE_ANIMATION;
        } else if (id == COMEDY.getId()) {
            name = MoviesApi.GENRE_COMEDY;
        } else if (id == ROMANCE.getId()) {
            name = MoviesApi.GENRE_ROMANCE;
        } else if (id == DRAMA.getId()) {
            name = MoviesApi.GENRE_DRAMA;
        } else if (id == SCIENCE_FICTION.getId()) {
            name = MoviesApi.GENRE_SCIENCE_FICTION;
        } else if (id == MUSIC.getId()) {
            name = MoviesApi.GENRE_MUSIC;
        } else if (id == THRILLER.getId()) {
            name = MoviesApi.GENRE_THRILLER;
        } else if (id == HORROR.getId()) {
            name = MoviesApi.GENRE_HORROR;
        } else if (id == DOCUMENTARY.getId()) {
            name = MoviesApi.GENRE_DOCUMENTARY;
        } else if (id == WAR.getId()) {
            name = MoviesApi.GENRE_WAR;
        } else if (id == SEARCH.getId()) {
            name = MoviesApi.FILTER_SEARCH;
        }
        return name;
    }

    //GreenDAO converter
    public static class FiltersConverter implements PropertyConverter<Filters, Long> {
        @Override
        public Filters convertToEntityProperty(Long databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            for (Filters role : Filters.values()) {
                if (role.getId() == databaseValue) {
                    return role;
                }
            }
            return Filters.POPULARITY;
        }

        @Override
        public Long convertToDatabaseValue(Filters entityProperty) {
            return entityProperty == null ? null : entityProperty.getId();
        }
    }

}
