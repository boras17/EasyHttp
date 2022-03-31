package redirect;

import Headers.Header;
import publishsubscribe.GenericCommunicate;

import java.util.List;

public class GenericError extends GenericCommunicate {
    private int status;
    private String msg;
    private ErrorType errorType;
    private List<Header> responseHeaders;

    public GenericError(int status,
                        List<Header> responseHeaders,
                        String errorMsg,
                        ErrorType errorType) {
        this.status = status;
        this.responseHeaders = responseHeaders;
        this.msg = errorMsg;
        this.errorType = errorType;
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

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "GenericError{" +
                "status=" + status +
                ", msg='" + msg + '\'' +
                ", errorType=" + errorType +
                ", responseHeaders=" + responseHeaders +
                '}';
    }
}
