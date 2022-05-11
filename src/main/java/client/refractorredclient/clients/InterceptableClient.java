package client.refractorredclient.clients;

import auth.AuthenticationProvider;
import client.ConnectionInitializr;
import client.refractorredclient.clients.builders.InterceptableClientBuilder;
import client.refractorredclient.clients.interceptingmodel.RequestInterceptors;
import client.refractorredclient.clients.interceptingmodel.ResponseInterceptorWrapper;
import client.refractorredclient.clients.interceptingmodel.ResponseInterceptors;
import client.refractorredclient.responsestatushandling.ResponseStatusHandler;
import cookies.CookieExtractor;
import intercepting.EasyRequestInterceptor;
import redirect.RedirectionHandler;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class InterceptableClient extends DefaultClient {

    public InterceptableClient(CookieExtractor cookieExtractor,
                               AuthenticationProvider authenticationProvider,
                               ConnectionInitializr connectionInitializr,
                               Duration connectionTimeout,
                               ResponseStatusHandler responseStatusHandler,
                               RedirectionHandler redirectionHandler) {
        super(cookieExtractor, authenticationProvider, connectionInitializr, connectionTimeout, responseStatusHandler, redirectionHandler);
    }

    public <T> EasyHttpResponse<T> sendAndIntercept(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler,
                                        ResponseInterceptors<T> responseInterceptors) {
        EasyHttpResponse<T> response = super.send(request, bodyHandler);

        this.interceptResponse(response, responseInterceptors);

        return response;
    }

    public <T> EasyHttpResponse<T> sendAndIntercept(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler,
                                        RequestInterceptors requestInterceptors) {
        this.interceptRequest(request, requestInterceptors);
        return super.send(request, bodyHandler);
    }

    public <T> EasyHttpResponse<T> sendAndIntercept(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler,
                                        RequestInterceptors requestInterceptors, ResponseInterceptors<T> responseInterceptors) {
        this.interceptRequest(request, requestInterceptors);
        EasyHttpResponse<T> response = super.send(request, bodyHandler);
        this.interceptResponse(response, responseInterceptors);
        return response;
    }

    public <T> CompletableFuture<EasyHttpResponse<T>> sendAsyncAndIntercept(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler, ExecutorService service,
                                                                            ResponseInterceptors<T> responseInterceptors) {
        return CompletableFuture.supplyAsync(() -> this.sendAndIntercept(request, bodyHandler, responseInterceptors), service);
    }

    public <T> CompletableFuture<EasyHttpResponse<T>> sendAsyncAndIntercept(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler, ExecutorService service,
                                                                            RequestInterceptors requestInterceptors) {
        return CompletableFuture.supplyAsync(() -> this.sendAndIntercept(request, bodyHandler, requestInterceptors), service);
    }

    public <T> CompletableFuture<EasyHttpResponse<T>> sendAsyncAndIntercept(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler, ExecutorService service,
                                                                            ResponseInterceptors<T> responseInterceptors, RequestInterceptors requestInterceptors) {
        return CompletableFuture.supplyAsync(() -> this.sendAndIntercept(request, bodyHandler, requestInterceptors, responseInterceptors), service);
    }

    private <T> EasyHttpResponse<T>  interceptResponse(EasyHttpResponse<T> response, ResponseInterceptors<T> responseInterceptors) {
        for (ResponseInterceptorWrapper<T> nextInterceptor : responseInterceptors) {
            nextInterceptor.getResponseInterceptor().handle(response);
        }
        return response;
    }

    private EasyHttpRequest interceptRequest(EasyHttpRequest request, RequestInterceptors requestInterceptors) {
        for(EasyRequestInterceptor requestInterceptor:requestInterceptors){
            requestInterceptor.handle(request);
        }
        return request;
    }

    public static InterceptableClientBuilder newBuilder(){
        return new InterceptableClientBuilder();
    }
}
