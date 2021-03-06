package client.responsestatushandling;

import client.connectiondata.ConnectionData;
import client.EasyHttpClient;
import redirect.AbstractRedirectionHandler;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.net.MalformedURLException;
import java.util.Optional;

public abstract class ResponseStatusHandler {
    private final EasyHttpClient client;

    protected ResponseStatusHandler(EasyHttpClient client){
        this.client = client;
    }

    public abstract <T> void handle2xxResponse(ConnectionData<T> connectionData);
    public abstract <T> void handle3xxResponse(ConnectionData<T> connectionData) throws MalformedURLException, UnsafeRedirectionException, RedirectionCanNotBeHandledException;
    public abstract <T> void handle4xxResponse(ConnectionData<T> connectionData);
    public abstract <T> void handle5xxResponse(ConnectionData<T> connectionData);

    protected EasyHttpClient getClient() {
        return client;
    }

    public void defaultRedirectionLogic(EasyHttpRequest request, EasyHttpClient client, EasyHttpResponse response) throws MalformedURLException, UnsafeRedirectionException, RedirectionCanNotBeHandledException {
        Optional<AbstractRedirectionHandler> redirectionHandlerOptional = client.getRedirectionHandler();
        boolean redirectionHandlerPresent = redirectionHandlerOptional.isPresent();
        if(redirectionHandlerPresent) {
            AbstractRedirectionHandler redirectionHandler = redirectionHandlerOptional.get();
            redirectionHandler.modifyRequest(request, response);
        }
    }
    protected  <T> void default4xxResponseStatusLogic(HttpURLConnection connection, AbstractBodyHandler<T> bodyHandler) {
        InputStream errorStream = connection.getErrorStream();
        bodyHandler.setInputStream(errorStream);
    }
    protected <T> void default5xxResponseStatusLogic(HttpURLConnection connection, AbstractBodyHandler<T> bodyHandler) {
        InputStream errorStream = connection.getErrorStream();
        bodyHandler.setInputStream(errorStream);
    }
    protected <T> void default2xxStatusLogic(HttpURLConnection connection, AbstractBodyHandler<T> bodyHandler) throws IOException {

        bodyHandler.setInputStream(connection.getInputStream());
    }
}
