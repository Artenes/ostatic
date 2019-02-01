package io.github.artenes.ostatic.model;

import java.util.Map;

/**
 * A song from an album
 */
public class Song {

    private final String albumId;
    private final String id;
    private final String cd;
    private final int track;
    private final String name;
    private final String time;
    private final Map<Format, FileInfo> files;
    private boolean isPlaying = false;

    public Song(String id, String cd, int track, String name, String time, Map<Format, FileInfo> files, String albumId) {
        this.id = id;
        this.cd = cd;
        this.track = track;
        this.name = name;
        this.time = time;
        this.files = files;
        this.albumId = albumId;
    }

    public String getId() {
        return id;
    }

    public String getCd() {
        return cd;
    }

    public int getTrack() {
        return track;
    }

    public String getName() {
        return name;
    }

    public Map<Format, FileInfo> getFiles() {
        return files;
    }

    public String getTime() {
        return time;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public String getAlbumId() {
        return albumId;
    }

}