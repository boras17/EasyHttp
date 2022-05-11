package client;

import httpenums.HttpStatus;

public class ResponseStatusLine {
    private int responseStatusInt;
    private HttpStatus responseStatusRange;

    public ResponseStatusLine(int responseStatusInt, HttpStatus responseStatusRange) {
        this.responseStatusInt = responseStatusInt;
        this.responseStatusRange = responseStatusRange;
    }

    public int getResponseStatusInt() {
        return responseStatusInt;
    }

    public void setResponseStatusInt(int responseStatusInt) {
        this.responseStatusInt = responseStatusInt;
    }

    public HttpStatus getResponseStatusRange() {
        return responseStatusRange;
    }

    public void setResponseStatusRange(HttpStatus responseStatusRange) {
        this.responseStatusRange = responseStatusRange;
    }
}
