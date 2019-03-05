package io.github.artenes.ostatic.api.model;

/**
 * Title and id of an album, only to display in search results
 */
public class ResumedAlbum {

    private final String id;
    private final String name;
    private final String cover;

    public ResumedAlbum(String id, String name, String cover) {
        this.id = id;
        this.name = name;
        this.cover = cover;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCover() {
        return cover;
    }
}
