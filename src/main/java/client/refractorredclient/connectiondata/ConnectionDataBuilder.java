package client.refractorredclient.connectiondata;

import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.net.HttpURLConnection;

public class ConnectionDataBuilder<T>{
    private HttpURLConnection connection;
    private AbstractBodyHandler<T> bodyHandler;
    private EasyHttpResponse<T> response;
    private EasyHttpRequest request;

    public ConnectionDataBuilder<T> connection(HttpURLConnection connection) {
        this.connection = connection;
        return this;
    }
    public ConnectionDataBuilder<T> bodyHandler(AbstractBodyHandler<T> bodyHandler) {
        this.bodyHandler = bodyHandler;
        return this;
    }
    public ConnectionDataBuilder<T> response(EasyHttpResponse<T> response) {
        this.response = response;
        return this;
    }
    public ConnectionDataBuilder<T> request(EasyHttpRequest request) {
        this.request = request;
        return this;
    }

    public ConnectionData<T> build() {
        return new ConnectionData<>(this.connection, this.bodyHandler, this.response, this.request);
    }
}
