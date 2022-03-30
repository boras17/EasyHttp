package redirect;

import Headers.Header;
import publishsubscribe.GenericCommunicate;

import java.util.List;

public class GenericError extends GenericCommunicate {
    private int status;
    private String msg;
    private List<Header> responseHeaders;

    public GenericError(int status, List<Header> responseHeaders, String errorMsg) {
        this.status = status;
        this.responseHeaders = responseHeaders;
        this.msg = errorMsg;
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
