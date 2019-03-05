package io.github.artenes.ostatic.api;

import io.github.artenes.ostatic.api.model.*;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A repository that holds songs from video games
 * It fetches all the data from downloads.khinsider.com
 * and uses fo htlm parser to read the data, since khinsider.com
 * does not provide an API
 */
public class KhinsiderRepository {

    private final JsoupHtmlDocumentReader htmlDocumentReader;

    public KhinsiderRepository(JsoupHtmlDocumentReader htmlDocumentReader) {
        this.htmlDocumentReader = htmlDocumentReader;
    }

    /**
     * Gets a list of albums by letter
     *
     * @param letter the letter to search for
     * @return the list of albums
     * @throws IOException in case of connection or parser error
     */
    public List<ResumedAlbum> getAlbumsByLetter(Letter letter) throws IOException, NullPointerException {

        if (letter == null) {
            throw new NullPointerException("Letter cannot be null");
        }

        String letterInString = letter == Letter.HASH ? "#" : letter.toString();

        Document page = htmlDocumentReader.readFromUrl(KhinsiderContract.getBrowseByLetterUrl(letterInString));

        ResultsPageParser resultsPageParser = new ResultsPageParser(page);

        try {

            return resultsPageParser.getResults();

        } catch (NullPointerException | IndexOutOfBoundsException exception) {

            throw new IOException("Error while parsing results", exception);

        }

    }

    /**
     * Search for albums with the given search parameter
     *
     * @param search the search parameter
     * @return the list of results. If none was found it returns an empty list
     * @throws IOException in case of connection or parser error
     */
    public List<ResumedAlbum> searchAlbums(String search) throws IOException {

        Document page = htmlDocumentReader.readFromUrl(KhinsiderContract.getSearchUrl(search));

        ResultsPageParser resultsPageParser = new ResultsPageParser(page);

        try {

            return resultsPageParser.getResults();

        } catch (NullPointerException | IndexOutOfBoundsException exception) {

            throw new IOException("Error while parsing results", exception);

        }

    }

    /**
     * Gets all the data of an album.
     *
     * @param id the id of the album. It is the slug of the album in the url.
     * @return the album
     * @throws IOException in case of connection or parser error
     */
    public Album getAlbum(String id) throws IOException {

        Document page = htmlDocumentReader.readFromUrl(KhinsiderContract.getAlbumUrl(id));

        AlbumPageParser albumPageParser = new AlbumPageParser(id, page);

        try {

            return albumPageParser.getAlbum();

        } catch (NullPointerException | IndexOutOfBoundsException exception) {

            throw new IOException("Error while parsing results", exception);

        }

    }

    public String getMp3LinkForSong(String songId) throws IOException {
        SongDownloadPageParser parser = new SongDownloadPageParser();

        Document songPage = htmlDocumentReader.readFromUrl(KhinsiderContract.getSongUrl(songId));

        return parser.getMp3Link(songPage);
    }

    public Album getAlbumWithDownloadLinks(String id) throws IOException {

        SongDownloadPageParser parser = new SongDownloadPageParser();

        try {

            Album album = getAlbum(id);

            for (Song song : album.getSongs()) {
                Document songPage = htmlDocumentReader.readFromUrl(KhinsiderContract.getSongUrl(song));
                String mp3UrlDownload = parser.getMp3Link(songPage);
                song.getFiles().get(Format.MP3).setUrl(mp3UrlDownload);
            }

            return album;

        } catch (NullPointerException | IndexOutOfBoundsException exception) {

            throw new IOException("Error while parsing results", exception);

        }

    }

    public void setMp3UrlOnSong(Song song) throws IOException {
        SongDownloadPageParser parser = new SongDownloadPageParser();
        Document songPage = htmlDocumentReader.readFromUrl(KhinsiderContract.getSongUrl(song));
        String mp3UrlDownload = parser.getMp3Link(songPage);
        song.getFiles().get(Format.MP3).setUrl(mp3UrlDownload);
    }

    /**
     * Get the available files to download for a song
     *
     * @param songId the id of the song, is the slug of the song in the donwload page
     *               plus the album name: album-name/music-name.mp3
     * @return a map with the available file formats to download
     * @throws IOException in case of connection or parser error
     */
    public Map<Format, File> getFiles(String songId) throws IOException {

        Document page = htmlDocumentReader.readFromUrl(KhinsiderContract.getAlbumUrl(songId));

        FilesPageParser filesPageParser = new FilesPageParser(page);

        try {

            return filesPageParser.getFiles();

        } catch (NullPointerException | IndexOutOfBoundsException exception) {

            throw new IOException("Error while parsing results", exception);

        }

    }

    /**
     * Get the top albums of the given type
     *
     * @param type the type of the top albums
     * @return the top albums list
     * @throws IOException in case of connection or parser error
     * @throws NullPointerException in case the provided type is null
     */
    public List<ResumedAlbum> getTopAlbums(TopAlbums type) throws IOException, NullPointerException {

        if (type == null) {
            throw new NullPointerException("Type cannot be null");
        }

        Document page = htmlDocumentReader.readFromUrl(KhinsiderContract.appendPath(type.toString()));

        TopAlbumsPageParser topAlbumsPageParser = new TopAlbumsPageParser(page);

        try {

            return topAlbumsPageParser.getTopAlbums();

        } catch (NullPointerException | IndexOutOfBoundsException exception) {

            throw new IOException("Error while parsing results", exception);

        }

    }

}
