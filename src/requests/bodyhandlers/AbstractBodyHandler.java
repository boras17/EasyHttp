package requests.bodyhandlers;

import Headers.Header;
import HttpEnums.HttpStatus;
import requests.easyresponse.EasyHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractBodyHandler<T> {
    private T body;
    private HttpStatus responseStatus;
    private List<Header> headers;
    private EasyHttpResponse<T> easyResponse;
    private InputStream inputStream;

    protected AbstractBodyHandler(){}

    protected abstract void calculateBody() throws IOException;

    public abstract EasyHttpResponse<T> getCalculatedResponse() throws IOException;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(HttpStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public EasyHttpResponse<T> getEasyResponse() {
        return easyResponse;
    }

    public void setEasyResponse(EasyHttpResponse<T> easyResponse) {
        this.easyResponse = easyResponse;
    }
}
