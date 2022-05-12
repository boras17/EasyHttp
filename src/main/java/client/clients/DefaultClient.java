package client.clients;

import auth.AuthenticationProvider;
import client.ConnectionInitializr;
import client.GenericClientBuilder;
import client.connectiondata.ConnectionData;
import client.connectiondata.ConnectionDataBuilder;
import client.EasyHttpClient;
import client.responsestatushandling.DefaultClientResponseStatusHandler;
import client.responsestatushandling.ResponseStatusHandler;
import cookies.CookieExtractor;
import redirect.AbstractRedirectionHandler;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class DefaultClient extends EasyHttpClient {

    @Override
    public <T> EasyHttpResponse<T> send(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler) {
        super.assertThatRequestAndBodyHandlerNotNull(request, bodyHandler);

        HttpURLConnection connection = super.openURLConnection(request);

        super.addHeadersToRequestObject(connection, request);
        super.copyBodyStream(request, connection);

        try{
            int responseStatus = super.initResponseHandlerFieldsAfterStatusExtracted(connection, bodyHandler);

            EasyHttpResponse<T> responseObject = bodyHandler.getCalculatedResponse();
            super.getCookieExtractor().ifPresent(cookieExtractor -> cookieExtractor.setCookies(responseObject));

            ConnectionData<T> connectionData = new ConnectionDataBuilder<T>()
                    .connection(connection)
                    .bodyHandler(bodyHandler)
                    .response(responseObject)
                    .request(request)
                    .build();

            super.handleResponseStatus(connectionData, responseStatus);
            return responseObject;
        }catch (IOException | UnsafeRedirectionException | RedirectionCanNotBeHandledException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> CompletableFuture<EasyHttpResponse<T>> sendAsync(EasyHttpRequest request,
                                                                AbstractBodyHandler<T> bodyHandler,
                                                                ExecutorService service) {
        return CompletableFuture.supplyAsync(() -> this.send(request, bodyHandler), service);
    }

    public DefaultClient(CookieExtractor cookieExtractor,
                         AuthenticationProvider authenticationProvider,
                         ConnectionInitializr connectionInitializr,
                         Duration connectionTimeout,
                         ResponseStatusHandler responseStatusHandler,
                         AbstractRedirectionHandler abstractRedirectionHandler) {
        super(cookieExtractor, authenticationProvider, connectionInitializr, connectionTimeout, responseStatusHandler, abstractRedirectionHandler);
    }
    public DefaultClient(CookieExtractor cookieExtractor,
                         AuthenticationProvider authenticationProvider,
                         ConnectionInitializr connectionInitializr,
                         Duration connectionTimeout,
                         AbstractRedirectionHandler abstractRedirectionHandler) {
        super(cookieExtractor, authenticationProvider, connectionInitializr, connectionTimeout, abstractRedirectionHandler);
    }

    public static  GenericClientBuilder<DefaultClient> newBuilder() {
        GenericClientBuilder<DefaultClient> defaultClientGenericClientBuilder = new GenericClientBuilder<>(DefaultClient.class);
        defaultClientGenericClientBuilder.setDecorator(new Function<DefaultClient, DefaultClient>() {
            @Override
            public DefaultClient apply(DefaultClient defaultClient) {
                 defaultClient.setResponseStatusHandler(new DefaultClientResponseStatusHandler(defaultClient));
                 return defaultClient;
            }
        });
        return defaultClientGenericClientBuilder;
    }
}
