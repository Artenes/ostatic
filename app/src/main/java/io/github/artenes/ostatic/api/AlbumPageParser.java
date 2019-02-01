package io.github.artenes.ostatic.api;

import io.github.artenes.ostatic.model.Album;
import io.github.artenes.ostatic.model.FileInfo;
import io.github.artenes.ostatic.model.Format;
import io.github.artenes.ostatic.model.Song;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser to fetch the data about an album from a html page
 */
class AlbumPageParser {

    private static final String N_A = "N/A";
    private static final int NOT_PRESENT = -1;
    private static final String ZERO = "0";
    private static final String NO_TIME = "00:00";
    private static final int AMOUNT_OF_CHARACTERS_UP_TO_THE_ID = 24;

    //regex to get any string that comes after "Released on: "
    //used to get the consoles where the games was released
    private static final Pattern RELEASED_ON_REGEX = Pattern.compile("(?<=Released on: ).*");

    private final String id;
    private final Document page;
    private Album cachedAlbum;

    public AlbumPageParser(String id, Document page) {
        this.page = page;
        this.id = id;
    }

    /**
     * Parse the album from the {@link Document} provided in the constructor
     *
     * @return the album with its available information
     * @throws NullPointerException      if a html element is not found
     * @throws IndexOutOfBoundsException if a html element is no present in a list
     */
    public Album getAlbum() throws NullPointerException, IndexOutOfBoundsException {

        if (cachedAlbum != null) {
            return cachedAlbum;
        }

        //the root div where all the content relies
        Element echoTopic = page.getElementById(KhinsiderContract.DIV_ECHO_TOPIC);

        //if this h2 exists, no album was found
        if (echoTopic.getElementsByTag("h2").get(0).text().equals(KhinsiderContract.NOT_FOUND_TITLE)) {
            return null;
        }

        //the data we need for an album
        String name;
        String numberOfFiles;
        String totalFilesize;
        String dateAdded;
        String totalTime;
        String releasedOn = N_A; //this value can be absent in some albums
        List<String> images;
        List<Song> songs;

        //the paragraph where the album details are
        //which is the second one inside the root div
        Element infoParagraph = echoTopic.getElementsByTag("p").get(1);

        //each detail in the paragraph is inside a <b> tag
        //so we just get all the <b> tags available and
        //fetch the data from them
        Elements details = infoParagraph.getElementsByTag("b");

        name = details.get(0).text();
        numberOfFiles = details.get(1).text();
        totalFilesize = details.get(2).text();
        dateAdded = details.get(3).text();

        //the total time is in the footer of the songs table
        //we get the footer, the the second th
        totalTime = echoTopic
                .getElementById(KhinsiderContract.TABLE_ROW_SONGLIST_FOOTER)
                .getElementsByTag("th").get(1).text();

        //the consoles where the game was release is inside the infoParagraph
        //but it is not inside a <b> tag, so we have to use a regex to extract
        //this data from the whole infoParagraph text
        Matcher matcher = RELEASED_ON_REGEX.matcher(infoParagraph.text());
        if (matcher.find()) {
            releasedOn = matcher.group();
        }

        //then we fetch the images
        images = getImages(echoTopic);

        //and the songs (this one is complicated)
        songs = getSongs(echoTopic);

        cachedAlbum = new Album(id, name, numberOfFiles, totalFilesize, dateAdded, releasedOn, totalTime, images, songs);

        return cachedAlbum;

    }

    /**
     * Fetch the available images of the album
     * from the given root element
     *
     * @param root the root element to search the images on
     * @return a list of urls pointing to the found images. An empty list is returned if none is found
     * @throws NullPointerException      if a html element is not found
     * @throws IndexOutOfBoundsException if a html element is no present in a list
     */
    private List<String> getImages(Element root) throws NullPointerException, IndexOutOfBoundsException {

        List<String> images = new ArrayList<>(0);

        //the first table in the root element contains the images
        Elements imagesLinks = root.getElementsByTag("table").get(0).getElementsByTag("a");

        for (Element link : imagesLinks) {
            images.add(link.attr("href"));
        }

        return images;

    }

    /**
     * Fetch the available songs of the album
     *
     * @param root the root element to search the songs on
     * @return a list of songs. And empty list is returned if none is found
     * @throws NullPointerException      if a html element is not found
     * @throws IndexOutOfBoundsException if a html element is no present in a list
     */
    private List<Song> getSongs(Element root) throws NullPointerException, IndexOutOfBoundsException {

        List<Song> songs = new ArrayList<>(0);

        Element songsTable = root.getElementById(KhinsiderContract.TABLE_SONGLIST);

        //this implementation is kinda complicated
        //the table that contains the music data has
        //various attributes absents or presents
        //depending on the game. Some have cd indexes
        //other only has mp3 data available. To solve
        //this we use these indexes to mark if a attribute
        //is present or not. By default all are not present
        int cdIndex = NOT_PRESENT;
        int trackIndex = NOT_PRESENT;
        int songNameIndex = NOT_PRESENT;
        int timeIndex = NOT_PRESENT;
        int mp3Index = NOT_PRESENT;
        int flacIndex = NOT_PRESENT;

        //this gets all the available attributes
        //which are the headers in the table
        List<String> headers = getAvailableHeadersInTable(songsTable);

        //this gets all the sounds, which are the rows in the table
        Elements rows = songsTable.getElementsByTag("tr");

        //now we will start marking those indexes with their position in the table
        //lets say the there album has the cd index attribute and it is the second
        //column in the table, so we mark the index with the value 1 (since it is zero based)
        //but if the cd index was not present and the second column is the song name
        //we would mark the song name index with 1 instead.
        //This loops passes by each table header and check its type.
        for (int index = 0; index < headers.size(); index++) {

            if (headers.get(index).equals(KhinsiderContract.CD)) {
                cdIndex = index;
                continue;
            }

            if (headers.get(index).equals(KhinsiderContract.TRACK)) {
                trackIndex = index;
                continue;
            }

            if (headers.get(index).equals(KhinsiderContract.SONG_NAME)) {
                songNameIndex = index;
                continue;
            }

            if (headers.get(index).equals(KhinsiderContract.TIME)) {
                timeIndex = index;
                continue;
            }

            if (headers.get(index).equals(KhinsiderContract.MP3)) {
                mp3Index = index;
                continue;
            }

            if (headers.get(index).equals(KhinsiderContract.FLAC)) {
                flacIndex = index;
            }

        }

        //now that we know the available attributes
        //we just have to get the data from the rows
        //we start at 1 and finishes at size - 1
        //to ignore the headers and footer rows
        for (int index = 1; index < rows.size() - 1; index++) {

            Elements data = rows.get(index).getElementsByTag("td");

            String cd = cdIndex != NOT_PRESENT ? data.get(cdIndex).text() : ZERO;
            String track = trackIndex != NOT_PRESENT ? data.get(trackIndex).text() : ZERO;
            String songName = songNameIndex != NOT_PRESENT ? data.get(songNameIndex).text() : ZERO;
            String time = timeIndex != NOT_PRESENT ? data.get(timeIndex).text() : NO_TIME;

            //the song id must not be a relative path, so we get only the part the it
            //is unique in the path provided in the table, that contains the album id
            //and the song id.
            String songId = songNameIndex != NOT_PRESENT ? data.get(songNameIndex)
                    .getElementsByTag("a").attr("href")
                    .substring(AMOUNT_OF_CHARACTERS_UP_TO_THE_ID) : "";

            //finally we get info about the song files
            Map<Format, FileInfo> files = new HashMap<>(0);

            if (mp3Index != NOT_PRESENT) {
                //the second argument is the size of the file
                files.put(Format.MP3, new FileInfo(Format.MP3, data.get(mp3Index).text()));
            }

            if (flacIndex != NOT_PRESENT) {
                files.put(Format.FLAC, new FileInfo(Format.FLAC, data.get(flacIndex).text()));
            }

            songs.add(new Song(songId, cd, index, songName, time, files, id));

        }

        return songs;

    }

    /**
     * Gets the text of the headers from the songs table
     * and put them in a list, so it can be manipulated more easily
     *
     * @param table the table to extract the headers from
     * @return the list of headers names
     * @throws NullPointerException if a html element is not found
     */
    private List<String> getAvailableHeadersInTable(Element table) throws NullPointerException {

        List<String> headersList = new ArrayList<>(0);

        Elements headers = table
                .getElementById(KhinsiderContract.TABLE_ROW_SONGLIST_HEADER)
                .getElementsByTag("th");

        for (Element header : headers) {

            String text = header.text();
            headersList.add(text);

            //this is a special case
            //the table contains the time duration of each song
            //but the corresponding table header does not exists
            //because the song name column uses column spam
            //so we have to put this here since is a attribute
            //that is available in all albums
            if (text.equals(KhinsiderContract.SONG_NAME)) {
                headersList.add(KhinsiderContract.TIME);
            }

        }

        return headersList;

    }

}
