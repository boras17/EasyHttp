package client.refractorredclient.clients;

import auth.AuthenticationProvider;
import client.ConnectionInitializr;
import client.refractorredclient.clients.builders.LoggableClientBuilder;
import client.refractorredclient.clients.loggingmodel.ClientSubscribers;
import client.refractorredclient.responsestatushandling.LoggableClientResponseStatusHandler;
import cookies.CookieExtractor;
import publishsubscribe.Operation;
import redirect.AbstractRedirectionHandler;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class LoggableClient extends DefaultClient {
    private Operation operation;
    private ClientSubscribers subscribers;

    public LoggableClient(CookieExtractor cookieExtractor,
                          AuthenticationProvider authenticationProvider,
                          ConnectionInitializr connectionInitializr,
                          Duration connectionTimeout, ClientSubscribers subscribers, AbstractRedirectionHandler abstractRedirectionHandler) {
        super(cookieExtractor, authenticationProvider, connectionInitializr, connectionTimeout, abstractRedirectionHandler);
        this.operation = new Operation();
        this.subscribers = subscribers;
        this.subscribers.initOperation(this.operation);
        super.setResponseStatusHandler(new LoggableClientResponseStatusHandler(this));
    }

    public <T> EasyHttpResponse<T> sendAndLog(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler) {
        return super.send(request,bodyHandler);
    }


    public <T> CompletableFuture<EasyHttpResponse<T>> sendAndLogAsync(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler, ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> this.sendAndLog(request, bodyHandler), service);
    }

    public Operation operation() {
        return operation;
    }

    public ClientSubscribers subscribers() {
        return subscribers;
    }

    public static LoggableClientBuilder newBuilder() {
        return new LoggableClientBuilder();
    }
}
