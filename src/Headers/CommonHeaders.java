package Headers;

public enum CommonHeaders {
    APPLICATION_JSON_HEADER(new Header("Content-Type", "application/json")),
    MULTIPART_FORM_DATA_HEADER(new Header("Content-Type", "multipart/form-data; boundary=myboundary"));

    private final Header header;

    CommonHeaders(Header header) {
        this.header = header;
    }

    public Header getHeader(){
        return this.header;
    }
}
