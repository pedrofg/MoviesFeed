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
            name = "popular";
        } else if (id == 1) {
            name = "upcoming";
        } else if (id == 2) {
            name = "now_playing";
        } else if (id == 3) {
            name = "top_rated";
        } else if (id == 4) {
            name = "revenue";
        } else if (id == 5) {
            name = "28";
        } else if (id == 6) {
            name = "16";
        } else if (id == 7) {
            name = "35";
        } else if (id == 8) {
            name = "10749";
        } else if (id == 9) {
            name = "18";
        } else if (id == 10) {
            name = "878";
        } else if (id == 11) {
            name = "10402";
        } else if (id == 12) {
            name = "53";
        } else if (id == 13) {
            name = "27";
        } else if (id == 14) {
            name = "99";
        } else if (id == 15) {
            name = "10752";
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
