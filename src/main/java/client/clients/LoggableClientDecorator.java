package client.clients;

import client.EasyHttpClient;
import client.clients.loggingmodel.ClientSubscribers;
import client.responsestatushandling.LoggableClientResponseStatusHandler;
import publishsubscribe.Operation;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class LoggableClientDecorator extends EasyHttpClient {
    private Operation operation;
    private ClientSubscribers subscribers;
    private EasyHttpClient client;

    public LoggableClientDecorator(EasyHttpClient client) {
        this.operation = new Operation();
        this.client = client;
        this.client.setResponseStatusHandler(new LoggableClientResponseStatusHandler(this));
    }

    public void configureClientSubscribers(ClientSubscribers clientSubscribers){
        this.subscribers = clientSubscribers;
        this.subscribers.initOperation(this.operation);
    }

    public Operation operation() {
        return operation;
    }

    public ClientSubscribers subscribers() {
        return subscribers;
    }

    @Override
    public <T> EasyHttpResponse<T> send(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler) {
        return this.client.send(request,bodyHandler);
    }

    @Override
    public <T> CompletableFuture<EasyHttpResponse<T>> sendAsync(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler, ExecutorService service) {
        return this.client.sendAsync(request,bodyHandler, service);
    }
}
