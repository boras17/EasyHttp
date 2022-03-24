package Headers;

public class Header {
    private String key;
    private String value;

    public Header(String key, String value) {
        this.value = value;
        this.key = key;
    }

    public Header() {}

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
}
