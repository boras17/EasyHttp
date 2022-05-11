package headers;

public enum CommonHeaders {
    APPLICATION_JSON_HEADER(new HttpHeader("Content-Type", "application/json")),
    MULTIPART_FORM_DATA_HEADER(new HttpHeader("Content-Type", "multipart/form-data; boundary=myboundary"));

    private final HttpHeader httpHeader;

    CommonHeaders(HttpHeader httpHeader) {
        this.httpHeader = httpHeader;
    }

    public HttpHeader getHeader(){
        return this.httpHeader;
    }
}
