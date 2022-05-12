package client.connectiondata;

import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.net.HttpURLConnection;

public class ConnectionData <T> {
    private HttpURLConnection connection;
    private AbstractBodyHandler<T> bodyHandler;
    private EasyHttpResponse<T> response;
    private EasyHttpRequest request;

    public ConnectionData(HttpURLConnection connection, AbstractBodyHandler<T> bodyHandler, EasyHttpResponse<T> response, EasyHttpRequest request) {
        this.connection = connection;
        this.bodyHandler = bodyHandler;
        this.response = response;
        this.request = request;
    }

    public ConnectionData() {

    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public AbstractBodyHandler<T> getBodyHandler() {
        return bodyHandler;
    }

    public void setBodyHandler(AbstractBodyHandler<T> bodyHandler) {
        this.bodyHandler = bodyHandler;
    }

    public EasyHttpResponse<T> getResponse() {
        return response;
    }

    public void setResponse(EasyHttpResponse<T> response) {
        this.response = response;
    }

    public EasyHttpRequest getRequest() {
        return request;
    }

    public void setRequest(EasyHttpRequest request) {
        this.request = request;
    }
}
