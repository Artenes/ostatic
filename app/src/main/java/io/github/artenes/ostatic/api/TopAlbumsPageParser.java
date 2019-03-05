package io.github.artenes.ostatic.api;

import io.github.artenes.ostatic.api.model.ResumedAlbum;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser to fetch the data from the table of top albums
 */
class TopAlbumsPageParser {

    private final Document page;
    private List<ResumedAlbum> cachedAlbums;

    public TopAlbumsPageParser(Document page) {
        this.page = page;
    }

    /**
     * Parse the top albums from the {@link Document} provided in the constructor
     *
     * @return the album with its available information
     * @throws NullPointerException if a html element is not found
     * @throws IndexOutOfBoundsException if a html element is no present in a list
     */
    public List<ResumedAlbum> getTopAlbums() throws NullPointerException, IndexOutOfBoundsException {

        if (cachedAlbums != null) {
            return cachedAlbums;
        }

        List<ResumedAlbum> albums = new ArrayList<>(0);

        //the table that lists the top albums
        Element table = page.getElementById(KhinsiderContract.TABLE_TOP_ALBUMS);

        //get the list of the top albums, which are the rows of the table
        Elements rows = table.getElementsByTag("tr");

        //we start from the second element to ignore the header row
        for (int index = 1; index < rows.size(); index++) {

            //get all the data for this row
            Elements data = rows.get(index).getElementsByTag("td");

            //this extracts the id of the album from a full url
            String[] urlParts = data.get(2).getElementsByTag("a").get(0).attr("href").split("/");
            //in this case the id is the last element of the url parts
            String id = urlParts[urlParts.length - 1];

            //get the name of the album
            String name = data.get(2).text();

            //get the cover for the album
            //the cover might not exist, so we have to check for this first
            String cover = null;
            Elements imageTags = data.get(0).getElementsByTag("img");
            if (imageTags != null && imageTags.size() > 0) {
                cover = imageTags.get(0).attr("src");
            }

            albums.add(new ResumedAlbum(id, name, cover));

        }

        return cachedAlbums = albums;

    }

}
