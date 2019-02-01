package io.github.artenes.ostatic.model;

public enum TopAlbums {

    ALL_TIME("all-time-top-100"),
    LAST_SIX_MOTHS("last-6-months-top-100"),
    NEWLY_ADDED("top-100-newly-added"),
    TOP_40("top40");

    private final String path;

    TopAlbums(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return path;
    }

}
