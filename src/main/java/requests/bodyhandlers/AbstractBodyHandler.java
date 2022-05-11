package requests.bodyhandlers;

import client.ResponseStatusLine;
import headers.HttpHeader;
import httpenums.HttpStatus;
import requests.EasyHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractBodyHandler<T> {
    private T body;
    private HttpStatus responseStatus;
    private List<HttpHeader> httpHeaders;
    private EasyHttpResponse<T> easyResponse;
    private InputStream inputStream;
    private ResponseStatusLine responseStatusLine;

    protected AbstractBodyHandler(){}

    public abstract EasyHttpResponse<T> getCalculatedResponse() throws IOException;

    public List<HttpHeader> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(List<HttpHeader> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public ResponseStatusLine getResponseStatusLine() {
        return responseStatusLine;
    }

    public void setResponseStatusLine(ResponseStatusLine responseStatusLine) {
        this.responseStatusLine = responseStatusLine;
    }

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

    public List<HttpHeader> getHeaders() {
        return httpHeaders;
    }

    public void setHeaders(List<HttpHeader> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public EasyHttpResponse<T> getEasyResponse() {
        return easyResponse;
    }

    public void setEasyResponse(EasyHttpResponse<T> easyResponse) {
        this.easyResponse = easyResponse;
    }
}
