package redirect;

import Headers.Header;
import publishsubscribe.GenericCommunicate;

import java.util.List;
import java.util.Optional;

public class GenericError extends GenericCommunicate {
    private int status;
    private String exceptionMg;
    private ErrorType errorType;
    private String serverMsg;
    private List<Header> responseHeaders;

    public GenericError(int status,
                        List<Header> responseHeaders,
                        String exceptionMg,
                        ErrorType errorType) {
        this.status = status;
        this.responseHeaders = responseHeaders;
        this.exceptionMg = exceptionMg;
        this.errorType = errorType;
    }
    public GenericError(int status,
                        List<Header> responseHeaders,
                        String exceptionMg,
                        ErrorType errorType,
                        String serverMsg) {
        this.status = status;
        this.responseHeaders = responseHeaders;
        this.exceptionMg = exceptionMg;
        this.errorType = errorType;
        this.serverMsg = serverMsg;
    }


    public Optional<String> getServerMessage(){
        return Optional.ofNullable(this.serverMsg);
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
        return exceptionMg;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public void setMsg(String msg) {
        this.exceptionMg = msg;
    }
    public void setServerMsg(String serverMsg){
        this.serverMsg = serverMsg;
    }
    @Override
    public String toString() {
        return "GenericError{" +
                "status=" + status +
                ", msg='" + exceptionMg + '\'' +
                ", errorType=" + errorType +
                ", responseHeaders=" + responseHeaders +
                '}';
    }
}
