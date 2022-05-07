package Headers;

public class HttpHeader {
    private String key;
    private String value;

    public HttpHeader(String key, String value) {
        this.value = value;
        this.key = key;
    }

    public HttpHeader() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Header{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
