package io.github.artenes.ostatic.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Wrapper around Jsoup to get the parsed DOM
 * from a html document. This is just to make
 * testing easier, since we can easily replace
 * with a mockup
 */
public class JsoupHtmlDocumentReader {

    /**
     * Read and parse the DOM from a html document
     * available in the web
     *
     * @param url the url where the html document is
     * @return the parsed DOM document
     * @throws IOException in case of connection error
     */
    public Document readFromUrl(String url) throws IOException {

        return Jsoup.connect(url).get();

    }

}
