package io.github.artenes.ostatic.api;

import io.github.artenes.ostatic.api.model.File;
import io.github.artenes.ostatic.api.model.Format;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser the fetch the available files to download for a song in a html page
 */
class FilesPageParser {

    private static final String EMPTY = "0.00 MB";

    //regex to get any string that is between two parentheses
    //used to get the size of the files
    private static final Pattern FILE_SIZE_REGEX = Pattern.compile("(?<=\\().*(?=\\))");

    private final Document page;
    private Map<Format, File> cachedFiles;

    public FilesPageParser(Document page) {
        this.page = page;
    }

    public Map<Format, File> getFiles() throws NullPointerException, IndexOutOfBoundsException {

        if (cachedFiles != null) {
            return cachedFiles;
        }

        Element echoTopic = page.getElementById(KhinsiderContract.DIV_ECHO_TOPIC);

        //if this h2 exists, no song was found
        if (echoTopic.getElementsByTag("h2").get(0).text().equals(KhinsiderContract.NOT_FOUND_TITLE)) {
            return null;
        }

        Map<Format, File> files = new HashMap<>(0);

        //we just assume that inside the echoTopic div, the third paragraph onwards
        //refers to all available files to download
        Element mp3Paragraph = echoTopic.getElementsByTag("p").get(3);
        Element flacParagraph = echoTopic.getElementsByTag("p").get(4);

        if (mp3Paragraph.text().contains(KhinsiderContract.MP3)) {

            String size = EMPTY;
            Matcher matcher = FILE_SIZE_REGEX.matcher(mp3Paragraph.text());
            if (matcher.find()) {
                size = matcher.group();
            }

            //the second argument is the url to download the file
            files.put(Format.MP3, new File(Format.MP3, size,
                    mp3Paragraph.getElementsByTag("a").attr("href")));

        }

        if (flacParagraph.text().contains(KhinsiderContract.FLAC)) {

            String size = EMPTY;
            Matcher matcher = FILE_SIZE_REGEX.matcher(flacParagraph.text());
            if (matcher.find()) {
                size = matcher.group();
            }

            files.put(Format.FLAC, new File(Format.FLAC, size,
                    flacParagraph.getElementsByTag("a").attr("href")));
        }

        cachedFiles = files;

        return cachedFiles;

    }

}
