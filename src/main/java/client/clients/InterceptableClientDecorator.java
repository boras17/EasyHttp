package client.clients;

import client.EasyHttpClient;
import client.clients.interceptingmodel.RequestInterceptors;
import client.clients.interceptingmodel.ResponseInterceptorWrapper;
import client.clients.interceptingmodel.ResponseInterceptors;
import intercepting.EasyRequestInterceptor;
import intercepting.EasyResponseInterceptor;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class InterceptableClientDecorator extends EasyHttpClient {
    private EasyHttpClient defaultClient;

    private ResponseInterceptors<?> responseInterceptors;
    private RequestInterceptors requestInterceptors;

    public InterceptableClientDecorator(EasyHttpClient defaultClient,ResponseInterceptors<?> responseInterceptors, RequestInterceptors requestInterceptors){
        super();
        this.defaultClient = defaultClient;
        this.responseInterceptors = responseInterceptors;
        this.requestInterceptors = requestInterceptors;
    }

    public InterceptableClientDecorator(EasyHttpClient defaultClient, ResponseInterceptors<?> responseInterceptors){
        this(defaultClient, responseInterceptors, new RequestInterceptors(Collections.emptyList()));
    }
    public InterceptableClientDecorator(EasyHttpClient defaultClient, RequestInterceptors requestInterceptors){
        this(defaultClient,  new ResponseInterceptors<>(Collections.emptyList()), requestInterceptors);
    }


    @Override
    public <ResponseType> EasyHttpResponse<ResponseType> send(EasyHttpRequest request, AbstractBodyHandler<ResponseType> bodyHandler) {
        System.out.println(request);
        for (EasyRequestInterceptor requestInterceptor : requestInterceptors) {
            requestInterceptor.handle(request);
        }
        EasyHttpResponse<ResponseType> response = this.defaultClient.send(request, bodyHandler);
        for (ResponseInterceptorWrapper<?> responseInterceptor : responseInterceptors) {
            EasyResponseInterceptor<ResponseType> interceptor =(EasyResponseInterceptor<ResponseType>) responseInterceptor.getResponseInterceptor();
            interceptor.handle(response);
        }
        return response;
    }

    @Override
    public <ResponseType> CompletableFuture<EasyHttpResponse<ResponseType>> sendAsync(EasyHttpRequest request, AbstractBodyHandler<ResponseType> bodyHandler, ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> this.send(request, bodyHandler), service);
    }
}
