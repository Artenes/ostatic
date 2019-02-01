package io.github.artenes.ostatic.model;

/**
 * Informations about a file. Does not provide url to download file
 */
public class FileInfo {

    private Format format;
    private String size;
    private String url;

    public FileInfo(Format format, String size) {
        this.format = format;
        this.size = size;
    }

    public Format getFormat() {
        return format;
    }

    public String getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
