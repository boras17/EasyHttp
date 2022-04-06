package requests.easyresponse;

import Headers.Header;
import HttpEnums.HttpStatus;

import java.util.List;

public class EasyHttpResponse<T> {
    private HttpStatus responseStatus;
    private int status;
    private T body;
    private List<Header> responseHeaders;

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(HttpStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Header> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(List<Header> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }
}
