package com.moviesfeed.api;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Pedro on 8/17/2016.
 */
public enum Filters {
    POPULARITY(0), UPCOMING(1), NOW_PLAYING(2), TOP_RATED(3), REVENUE(4),
    ACTION(5), ANIMATION(6), COMEDY(7), ROMANCE(8), DRAMA(9), SCIENCE_FICTION(10),
    MUSIC(11), THRILLER(12), HORROR(13), DOCUMENTARY(14), WAR(15);

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
        if (id == 0) {
            name = MoviesApi.FILTER_POPULARITY;
        } else if (id == 1) {
            name = MoviesApi.FILTER_UPCOMING;
        } else if (id == 2) {
            name = MoviesApi.FILTER_NOW_PLAYING;
        } else if (id == 3) {
            name = MoviesApi.FILTER_TOP_RATED;
        } else if (id == 4) {
            name = MoviesApi.FILTER_REVENUE;
        } else if (id == 5) {
            name = MoviesApi.GENRE_ACTION;
        } else if (id == 6) {
            name = MoviesApi.GENRE_ANIMATION;
        } else if (id == 7) {
            name = MoviesApi.GENRE_COMEDY;
        } else if (id == 8) {
            name = MoviesApi.GENRE_ROMANCE;
        } else if (id == 9) {
            name = MoviesApi.GENRE_DRAMA;
        } else if (id == 10) {
            name = MoviesApi.GENRE_SCIENCE_FICTION;
        } else if (id == 11) {
            name = MoviesApi.GENRE_MUSIC;
        } else if (id == 12) {
            name = MoviesApi.GENRE_THRILLER;
        } else if (id == 13) {
            name = MoviesApi.GENRE_HORROR;
        } else if (id == 14) {
            name = MoviesApi.GENRE_DOCUMENTARY;
        } else if (id == 15) {
            name = MoviesApi.GENRE_WAR;
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
                if (role.id == databaseValue) {
                    return role;
                }
            }
            return Filters.POPULARITY;
        }

        @Override
        public Long convertToDatabaseValue(Filters entityProperty) {
            return entityProperty == null ? null : entityProperty.id;
        }
    }

}
