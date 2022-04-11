package client;

import Headers.Header;
import HttpEnums.HttpStatus;
import auth.AuthenticationProvider;
import exceptions.RequestObjectRequiredException;
import exceptions.ResponseHandlerRequired;
import intercepting.EasyRequestInterceptor;
import intercepting.EasyResponseInterceptor;
import intercepting.Interceptor;
import publishsubscribe.Channels;
import publishsubscribe.Event;
import publishsubscribe.Operation;
import publishsubscribe.communcates.ErrorCommunicate;
import publishsubscribe.errorsubscriberimpl.Subscriber;
import redirect.*;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.RedirectionUnhandled;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.cookies.CookieExtractor;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;
import requests.multirpart.simplerequest.jsonsender.BodyProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EasyHttp {
    private String userAgent;
    private CookieExtractor cookieExtractor;
    private AuthenticationProvider authenticationProvider;
    private RedirectionHandler redirectionHandler;
    private Duration connectionTimeout;
    private Map<String, Subscriber> subscribedChannels;
    private ConnectionInitializr connectionInitializr;
    private Operation operation;
    private Map<Integer, EasyResponseInterceptor<?>> responseInterceptors = new TreeMap<>();
    private EasyRequestInterceptor requestIInterceptor;

    public <T>CompletableFuture<EasyHttpResponse<T>> sendAsync(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler){
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.send(request, bodyHandler);
            } catch (RedirectionUnhandled redirectionUnhandled){
                Event.operation.publish(Channels.REDIRECT_ERROR_CHANNEL,redirectionUnhandled.getGenericError());
                redirectionUnhandled.printStackTrace();
                return new EasyHttpResponse<>();
            }
            catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
                return new EasyHttpResponse<>();
            }
        });
    }

    private void addAuthHeaderIfProviderPresent(EasyHttpRequest request){
        Optional<AuthenticationProvider> provider = Optional.ofNullable(this.authenticationProvider);
        if(provider.isPresent()){
            AuthenticationProvider authenticationProvider = provider.get();
            for(Header header: authenticationProvider.getAuthHeaders()){
                request.getHeaders().add(header);
            }
        }
    }

    public <T> EasyHttpResponse<T> send(requests.multirpart.simplerequest.EasyHttpRequest request,
                                 AbstractBodyHandler<T> bodyHandler)
            throws IOException, IllegalAccessException, RedirectionUnhandled {
        if(request == null) {
            throw new RequestObjectRequiredException("Request object can not be null");
        }
        if(bodyHandler == null){
            throw new ResponseHandlerRequired("Response handler can not be null");
        }

        this.getRequestIInterceptor().ifPresent(interceptor -> {
            interceptor.handle(request);
        });

        HttpURLConnection connection = this.connectionInitializr.openConnection(request);

        this.getConnectionTimeout().ifPresentOrElse(timeout -> {
            int timeoutMillis = (int)timeout.toMillis();
            connection.setConnectTimeout(timeoutMillis);
        }, ()-> connection.setConnectTimeout(1000 * 30));

        List<Header> requestHeaders = request.getHeaders();

        if(requestHeaders.size() > 0){
            for(Header header: requestHeaders){
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        this.addAuthHeaderIfProviderPresent(request);

        Optional<BodyProvider<?>> bodyConverter = request.getBody();

        if(bodyConverter.isPresent()){
            OutputStream connectionOutputStream = connection.getOutputStream();
            BodyProvider<?> converter = bodyConverter.get();
            converter.setOutputStream(connectionOutputStream);
            converter.prepareAndCopyToStream();
            connectionOutputStream.close();
        }

        int responseStatus = connection.getResponseCode();

        Map<String, List<String>> headersFields = connection.getHeaderFields();
        List<Header> headers = this.calculateHeaders(headersFields);

        if(responseStatus >= 200 && responseStatus < 300){
            bodyHandler.setInputStream(connection.getInputStream());
        }
        else if(responseStatus >= 400 && responseStatus < 500){
            InputStream errorStream = connection.getErrorStream();
            bodyHandler.setInputStream(errorStream);
            GenericError genericError = new GenericError(responseStatus,
                    headers,
                    "Server responded with client error status: " +responseStatus,
                    ErrorType.CLIENT,
                    errorStream == null ? "No message from server": new String(errorStream.readAllBytes()));
            boolean userSubscribedClientErrors = this.getSubscribedChannels()
                    .containsKey(Channels.CLIENT_ERROR_CHANNEL);
            if(userSubscribedClientErrors){
                this.operation.publish(Channels.CLIENT_ERROR_CHANNEL, new ErrorCommunicate(genericError));
            }
        }else if(responseStatus >= 500){
            InputStream errorStream = connection.getErrorStream();
            bodyHandler.setInputStream(errorStream);
            GenericError genericError = new GenericError(responseStatus,
                    headers,
                    "Server responded with server error status: " +responseStatus,
                    ErrorType.SERVER,
                    new String(errorStream.readAllBytes()));
            this.operation.publish(Channels.SERVER_ERROR_CHANNEL, new ErrorCommunicate(genericError));
        }

        bodyHandler.setResponseStatus(getHttpStatus(responseStatus));
        bodyHandler.setHeaders(headers);

        EasyHttpResponse<T> _response = bodyHandler.getCalculatedResponse();

        this.getResponseInterceptors()
                        .ifPresent(interceptorsWithOrderMap -> {
                            for(Map.Entry<Integer, EasyResponseInterceptor<?>> interceptorEntry:  interceptorsWithOrderMap.entrySet()) {
                                EasyResponseInterceptor<T> interceptor=(EasyResponseInterceptor<T>)interceptorEntry.getValue();
                                interceptor.handle(_response);
                            }
                        });

        _response.setStatus(responseStatus);
        if(responseStatus >= 300 && responseStatus < 400){

            this.operation.publish(Channels.REDIRECT_NOTIFICATION, new GenericNotification(LocalDateTime.now(),
                    "An redirection occured",
                    _response.getResponseHeaders(),
                    request.getUrl().toString(), NotificationTypes.REDIRECT));

            RedirectionHandler redirectionHandler
                    = this.getRedirectionHandler()
                    .orElseThrow(() -> {
                        GenericError genericError = new GenericError(responseStatus,
                                headers, "Server respond with redirect status: " + responseStatus + "and you did not provide redirection handler",
                                ErrorType.REDIRECT,
                                null);
                        this.operation.publish(Channels.REDIRECT_ERROR_CHANNEL, genericError);
                        try{
                            genericError.setServerMsg(new String(connection.getErrorStream().readAllBytes()));
                        }catch (java.io.IOException e){
                            e.printStackTrace();
                        }

                        return new RedirectionUnhandled(genericError);
                    });
            try{
                redirectionHandler.modifyRequest(request, _response);
            }catch (UnsafeRedirectionException  e) {
                this.operation.publish(Channels.REDIRECT_ERROR_CHANNEL, e.getGenericError());
                e.printStackTrace();
            }catch (RedirectionCanNotBeHandledException e) {
                this.operation.publish(Channels.REDIRECT_ERROR_CHANNEL, e.getGenericError());
                e.printStackTrace();
            }
        }
        this.getCookieExtractor().ifPresent(cookieExtractor -> {
            cookieExtractor.setCookies(_response);
        });
        return _response; //bodyHandler.getCalculatedResponse();
    }

    private List<Header> calculateHeaders(Map<String, List<String>> headersFields) {
        return headersFields
                .entrySet()
                .stream()
                .filter(entry -> {
                    return entry.getKey()!=null;
                })
                .map((entry) -> {
                    return new Header(entry.getKey(), entry.getValue().get(0));

                }).collect(Collectors.toList());
    }

    private HttpStatus getHttpStatus(int responseCode) {
        if(responseCode>=200 && responseCode<300){
            return HttpStatus.SUCCESSFUL;
        }else if(responseCode>=300 && responseCode<400){
            return HttpStatus.REDIRECTED;
        }else if(responseCode >= 400 && responseCode <= 500){
            return HttpStatus.CLIENT_ERROR;
        }else {
            return HttpStatus.SERVER_ERROR;
        }
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }


    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public Map<String, Subscriber> getSubscribedChannels() {
        return subscribedChannels;
    }

    public void setSubscribedChannels(Map<String, Subscriber> subscribedChannels) {
        this.subscribedChannels = subscribedChannels;
        subscribedChannels.forEach((key, value) -> {
            this.operation.subscribe(key, value);
        });
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Optional<CookieExtractor> getCookieExtractor() {
        return Optional.ofNullable(cookieExtractor);
    }

    public void setCookieExtractor(CookieExtractor cookieExtractor) {
        this.cookieExtractor = cookieExtractor;
    }

    public Optional<Map<Integer,EasyResponseInterceptor<?>>> getResponseInterceptors() {
        return Optional.ofNullable(responseInterceptors);
    }

    public Optional<Interceptor<EasyHttpRequest>> getRequestIInterceptor() {
        return Optional.ofNullable(this.requestIInterceptor);
    }

    public Optional<RedirectionHandler> getRedirectionHandler() {
        return Optional.ofNullable(redirectionHandler);
    }

    public void setRedirectionHandler(RedirectionHandler redirectionHandler) {
        this.redirectionHandler = redirectionHandler;
    }

    public Optional<Duration> getConnectionTimeout() {
        return Optional.ofNullable(connectionTimeout);
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public ConnectionInitializr getConnectionInitializr() {
        return connectionInitializr;
    }

    public void setConnectionInitializr(ConnectionInitializr connectionInitializr) {
        this.connectionInitializr = connectionInitializr;
    }

    public EasyHttp(ConnectionInitializr connectionInitializr) {
        this.connectionInitializr = connectionInitializr;
    }

    public void addResponseInterceptor(EasyResponseInterceptor<?> easyResponseInterceptor, int order) {
        this.responseInterceptors.put(order, easyResponseInterceptor);
    }

    public void setRequestInterceptor(EasyRequestInterceptor easyRequestInterceptor){
        this.requestIInterceptor = easyRequestInterceptor;
    }
    public EasyHttp(Operation operation, ConnectionInitializr connectionInitializr) {
        this.operation = operation;
        this.connectionInitializr = connectionInitializr;
    }
    public EasyHttp(){

    }
}
