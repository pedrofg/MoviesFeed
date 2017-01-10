package com.moviesfeed.api;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * Created by Pedro on 8/17/2016.
 */
public enum Filters {
    POPULARITY(0), REVENUE(1), RELEASE_DATE(2),
    TOP_RATED(3), UPCOMING(4);

//    POPULARITY("popularity"), REVENUE("revenue"), RELEASE_DATE("release_date"),
//    TOP_RATED("top_rated"), UPCOMING("upcoming");

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
            name = "popularity";
        } else if (id == 1) {
            name = "revenue";
        } else if (id == 2) {
            name = "release_date";
        } else if (id == 3) {
            name = "top_rated";
        } else if (id == 4) {
            name = "upcoming";
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
