package requests.bodyhandlers;

import Headers.HttpHeader;
import HttpEnums.HttpStatus;
import requests.easyresponse.EasyHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AbstractBodyHandler<T> {
    private T body;
    private HttpStatus responseStatus;
    private List<HttpHeader> httpHeaders;
    private EasyHttpResponse<T> easyResponse;
    private InputStream inputStream;

    protected AbstractBodyHandler(){}

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
