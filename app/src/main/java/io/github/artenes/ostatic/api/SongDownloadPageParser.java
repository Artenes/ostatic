package io.github.artenes.ostatic.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Parser to fetch the download data from a song
 */
class SongDownloadPageParser {

    public String getMp3Link(Document page) throws NullPointerException, IndexOutOfBoundsException {

        Element div = page.getElementById(KhinsiderContract.DIV_ECHO_TOPIC);

        Element paragraph = div.getElementsByTag("p").get(3);

        Element anchor = paragraph.getElementsByTag("a").get(0);

        return anchor.attr("href");

    }

}
