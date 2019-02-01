package io.github.artenes.ostatic.api;

import io.github.artenes.ostatic.model.ResumedAlbum;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser to fetch the data from a results page
 */
class ResultsPageParser {

    private final Document page;
    private List<ResumedAlbum> cachedAlbums;

    public ResultsPageParser(Document page) {
        this.page = page;
    }

    /**
     * Parse the search results from the {@link Document} provided in the constructor
     *
     * @return the album with its available information
     * @throws NullPointerException if a html element is not found
     * @throws IndexOutOfBoundsException if a html element is no present in a list
     */
    public List<ResumedAlbum> getResults() throws NullPointerException, IndexOutOfBoundsException {

        if (cachedAlbums != null)
            return cachedAlbums;

        Element echoTopic = page.getElementById(KhinsiderContract.DIV_ECHO_TOPIC);

        //if the page has these texts in it, that means that nothing was found
        if (echoTopic.getElementsByTag("p")
                .get(0).text().equals("Found 0 matching results.")
                || echoTopic.getElementsByTag("h2")
                .get(0).text().equals(KhinsiderContract.NOT_FOUND_TITLE)) {
            return null;
        }

        List<ResumedAlbum> albums = new ArrayList<>(0);

        Elements paragraphs = echoTopic.getElementsByTag("p");

        //we expect that in a list of albums in the html page
        //should exists at least two paragraphs inside the EchoTopic div
        //the second one would hold the list of albums
        //if there is less paragraphs than this, just return an empty list
        //because probably no albums are rendered in the page
        if (paragraphs.size() < 2) {
            return albums;
        }

        //this gets the links that point out to the albums
        //each link has the name of the album and its url
        Elements albumsLinks = paragraphs.get(1).getElementsByTag("a");

        for (Element link : albumsLinks) {

            //we want only the name of the game as the id, not the relative url
            String[] linkParts = link.attr("href").split("/");
            //the last part of this url contains the name of the game
            String id = linkParts[linkParts.length - 1];

            String title = link.text();

            albums.add(new ResumedAlbum(id, title, null));

        }

        return cachedAlbums = albums;

    }

}
