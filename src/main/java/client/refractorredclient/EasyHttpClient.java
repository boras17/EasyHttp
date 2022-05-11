package client.refractorredclient;

import auth.AuthenticationProvider;
import client.ConnectionInitializr;
import client.ResponseStatusLine;
import client.refractorredclient.connectiondata.ConnectionData;
import client.refractorredclient.responsestatushandling.ResponseStatusHandler;
import client.refractorredclient.responsestatushandling.ResponseStatusStrategySwitcher;
import cookies.CookieExtractor;
import exceptions.RequestObjectRequiredException;
import exceptions.ResponseHandlerRequired;
import headers.HttpHeader;
import httpenums.HttpStatus;
import redirect.AbstractRedirectionHandler;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.bodyproviders.BodyProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.time.Duration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public abstract class EasyHttpClient {
    private CookieExtractor cookieExtractor;
    private AuthenticationProvider authenticationProvider;
    private ConnectionInitializr connectionInitializr;
    private Duration connectionTimeout;
    private AbstractRedirectionHandler redirectionHandler;
    private ResponseStatusHandler responseStatusHandler;

    public abstract <T> EasyHttpResponse<T> send(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler);
    public abstract <T> CompletableFuture<EasyHttpResponse<T>> sendAsync(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler, ExecutorService service);

    protected void addHeadersToRequestObject(HttpURLConnection connection, EasyHttpRequest request){
        List<HttpHeader> headers = request.getHeaders();
        this.getAuthenticationProvider().ifPresent(provider -> {
            headers.add(provider.getAuthHeaders());
        });
        if(headers == null || headers.size() == 0){
            return;
        }
        for(HttpHeader header: headers){
            connection.setRequestProperty(header.getKey(), header.getValue());
        }
    }

    protected HttpURLConnection openURLConnection(EasyHttpRequest request) {
        HttpURLConnection connection = this.getConnectionInitializr().openConnection(request);

        Duration connectionTimeout = this.getConnectionTimeout();
        int connectionTimoutMillis = (int)connectionTimeout.toMillis();
        connection.setConnectTimeout(connectionTimoutMillis);
        return connection;
    }

    protected void copyBodyStream(EasyHttpRequest request, HttpURLConnection connection){
        request.getBody().ifPresent(bodyProvider -> {
            try {
                this.copyRequestDataIntoConnectionStream(bodyProvider, connection);
            } catch (IOException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected <T> void assertThatRequestAndBodyHandlerNotNull(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler) {
        if(request == null) {
            throw new RequestObjectRequiredException("Request object can not be null");
        }
        if(bodyHandler == null){
            throw new ResponseHandlerRequired("Response handler can not be null");
        }
    }

    protected <T> void copyRequestDataIntoConnectionStream(BodyProvider<T> bodyProvider, HttpURLConnection connection) throws IOException, IllegalAccessException {
        OutputStream connectionOutputStream = connection.getOutputStream();
        bodyProvider.setOutputStream(connectionOutputStream);
        bodyProvider.prepareAndCopyToStream();
        connectionOutputStream.close();
    }

    protected <T> int initResponseHandlerFieldsAfterStatusExtracted(HttpURLConnection connection, AbstractBodyHandler<T> bodyHandler) throws IOException {
        int responseStatus = connection.getResponseCode();
        Map<String, List<String>> headersFields = connection.getHeaderFields();
        List<HttpHeader> httpHeaders = this.calculateResponseHeaders(headersFields);
        bodyHandler.setResponseStatusLine(this.getResponseStatusLine(responseStatus));
        bodyHandler.setHeaders(httpHeaders);
        bodyHandler.setInputStream(connection.getInputStream());
        return responseStatus;
    }

    protected<T> void handleResponseStatus(ConnectionData<T> connectionData, int responseStatus) throws MalformedURLException, UnsafeRedirectionException, RedirectionCanNotBeHandledException {
        ResponseStatusStrategySwitcher<T> statusSwitcher = new ResponseStatusStrategySwitcher<>(this.getResponseStatusHandler(),connectionData);
        statusSwitcher.handleResponseStatus(responseStatus);
    }

    private ResponseStatusLine getResponseStatusLine(int responseStatus){
        return new ResponseStatusLine(responseStatus, HttpStatus.getStatus(responseStatus));
    }

    protected List<HttpHeader> calculateResponseHeaders(Map<String, List<String>> headersFields) {
        return headersFields
                .entrySet()
                .stream()
                .filter(entry -> {
                    return entry.getKey()!=null;
                })
                .map((entry) -> {
                    return new HttpHeader(entry.getKey(), entry.getValue().get(0));

                }).collect(Collectors.toList());
    }

    public EasyHttpClient(CookieExtractor cookieExtractor,
                          AuthenticationProvider authenticationProvider,
                          ConnectionInitializr connectionInitializr,
                          Duration connectionTimeout,
                          ResponseStatusHandler responseStatusHandler,
                          AbstractRedirectionHandler abstractRedirectionHandler) {
        this.cookieExtractor = cookieExtractor;
        this.authenticationProvider = authenticationProvider;
        this.connectionInitializr = connectionInitializr;
        this.connectionTimeout = connectionTimeout;
        this.responseStatusHandler = responseStatusHandler;
        this.redirectionHandler = abstractRedirectionHandler;
    }
    public EasyHttpClient(CookieExtractor cookieExtractor,
                          AuthenticationProvider authenticationProvider,
                          ConnectionInitializr connectionInitializr,
                          Duration connectionTimeout,
                          AbstractRedirectionHandler abstractRedirectionHandler) {
        this.cookieExtractor = cookieExtractor;
        this.authenticationProvider = authenticationProvider;
        this.connectionInitializr = connectionInitializr;
        this.connectionTimeout = connectionTimeout;
        this.redirectionHandler = abstractRedirectionHandler;
    }

    public Optional<CookieExtractor> getCookieExtractor() {
        return Optional.ofNullable(this.cookieExtractor);
    }

    public void setCookieExtractor(CookieExtractor cookieExtractor) {
        this.cookieExtractor = cookieExtractor;
    }

    public Optional<AuthenticationProvider> getAuthenticationProvider() {
        return Optional.ofNullable(authenticationProvider);
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public ConnectionInitializr getConnectionInitializr() {
        return connectionInitializr;
    }

    public void setConnectionInitializr(ConnectionInitializr connectionInitializr) {
        this.connectionInitializr = connectionInitializr;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout == null ? Duration.ofSeconds(10) : this.connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Optional<AbstractRedirectionHandler> getRedirectionHandler() {
        return Optional.ofNullable(this.redirectionHandler);
    }

    public void setRedirectionHandler(AbstractRedirectionHandler redirectionHandler) {
        this.redirectionHandler = redirectionHandler;
    }

    public ResponseStatusHandler getResponseStatusHandler() {
        return responseStatusHandler;
    }

    public void setResponseStatusHandler(ResponseStatusHandler responseStatusHandler) {
        this.responseStatusHandler = responseStatusHandler;
    }
}
