package client.refractorredclient.clients;

import auth.AuthenticationProvider;
import client.ConnectionInitializr;
import client.refractorredclient.connectiondata.ConnectionData;
import client.refractorredclient.connectiondata.ConnectionDataBuilder;
import client.refractorredclient.AbstractClientBuilder;
import client.refractorredclient.EasyHttpClient;
import client.refractorredclient.responsestatushandling.ResponseStatusHandler;
import cookies.CookieExtractor;
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
                         ResponseStatusHandler responseStatusHandler) {
        super(cookieExtractor, authenticationProvider, connectionInitializr, connectionTimeout, responseStatusHandler);
    }
    public DefaultClient(CookieExtractor cookieExtractor,
                         AuthenticationProvider authenticationProvider,
                         ConnectionInitializr connectionInitializr,
                         Duration connectionTimeout) {
        super(cookieExtractor, authenticationProvider, connectionInitializr, connectionTimeout);
    }

    public static AbstractClientBuilder newBuilder() {
        return new AbstractClientBuilder();
    }
}
