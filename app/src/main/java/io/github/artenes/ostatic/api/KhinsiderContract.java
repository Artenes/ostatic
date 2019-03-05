package io.github.artenes.ostatic.api;

import io.github.artenes.ostatic.api.model.Song;

/**
 * Class with constants and utility methods to deal
 * with Khinsider website content and urls
 */
final class KhinsiderContract {

    //Common HTML elements ids
    static final String DIV_ECHO_TOPIC = "EchoTopic";
    static final String TABLE_SONGLIST = "songlist";
    static final String TABLE_ROW_SONGLIST_FOOTER = "songlist_footer";
    static final String TABLE_ROW_SONGLIST_HEADER = "songlist_header";
    static final String TABLE_TOP_ALBUMS = "top40";

    //Common HTML elements values
    static final String CD = "CD";
    static final String TRACK = "Track";
    static final String SONG_NAME = "Song Name";
    static final String TIME = "Time";
    static final String MP3 = "MP3";
    static final String FLAC = "FLAC";

     static final String NOT_FOUND_TITLE = "Ooops!";

    private static final String BASE_URL = "https://downloads.khinsider.com";
    private static final String BROWSE_URL = BASE_URL + "/game-soundtracks/browse";
    private static final String SEARCH_URL = BASE_URL + "/search?search=";
    private static final String ALBUM_URL = BASE_URL + "/game-soundtracks/album";

    static String getBrowseByLetterUrl(String letter) {
        return BROWSE_URL + "/" + letter;
    }

    static String getSearchUrl(String search) {
        return SEARCH_URL + search;
    }

    static String getAlbumUrl(String id) {
        return ALBUM_URL + "/" + id;
    }

    static String appendPath(String path) {
        return BASE_URL + "/" + path;
    }

    static String getSongUrl(Song song) {
        return getSongUrl(song.getId());
    }

    static String getSongUrl(String songId) {
        return ALBUM_URL + "/" + songId;
    }

    private KhinsiderContract() {
    }

}
