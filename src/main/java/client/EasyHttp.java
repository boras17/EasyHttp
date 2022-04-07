package client;

import Headers.Header;
import HttpEnums.HttpStatus;
import HttpEnums.Method;
import auth.AuthenticationProvider;
import intercepting.Interceptor;
import publishsubscribe.Channels;
import publishsubscribe.Event;
import publishsubscribe.communcates.Communicate;
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
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EasyHttp {
    private URL url;
    private Method method;
    private HttpURLConnection connection;
    private String userAgent;
    private CookieExtractor cookieExtractor;
    private AuthenticationProvider authenticationProvider;
    private RedirectionHandler redirectionHandler;
    private Duration connectionTimeout;
    private Map<String, Subscriber> subscribedChannels;

    private Interceptor<EasyHttpResponse<?>> responseInterceptor;
    private Interceptor<EasyHttpRequest> requestIInterceptor;

    public Map<String, List<String>> extractHeaders(String headerName){
        return this.connection.getHeaderFields().entrySet()
                .stream()
                .filter(x-> x.getKey().equals(headerName))
                .findFirst()
                .map(entry -> Collections.singletonMap(entry.getKey(), entry.getValue()))
                .orElseThrow();
    }

    public <T>CompletableFuture<EasyHttpResponse<T>> sendAsync(EasyHttpRequest request, AbstractBodyHandler<T> bodyHandler){
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.send(request, bodyHandler);
            } catch (RedirectionUnhandled redirectionUnhandled){
                Event.operation.publish(Channels.ERROR_CHANNEL,redirectionUnhandled.getGenericError());
                redirectionUnhandled.printStackTrace();
                return new EasyHttpResponse<>();
            }
            catch (IOException | IllegalAccessException e) {
                e.printStackTrace();
                return new EasyHttpResponse<>();
            }
        });
    }

    private void addAuthHeaderIfProviderPresent(EasyHttpRequest easyHttpRequest, HttpURLConnection connection){
        Optional<AuthenticationProvider> provider = Optional.ofNullable(this.authenticationProvider);
        if(provider.isPresent()){
            AuthenticationProvider authenticationProvider = provider.get();
            for(Header header: authenticationProvider.getAuthHeaders()){
                connection.setRequestProperty(header.getKey(),header.getValue());
            }
        }
    }

    public <T> EasyHttpResponse<T> send(requests.multirpart.simplerequest.EasyHttpRequest request,
                                 AbstractBodyHandler<T> bodyHandler)
            throws IOException, IllegalAccessException, RedirectionUnhandled {

        this.getRequestIInterceptor().ifPresent(interceptor -> {
            System.out.println("present");
            interceptor.handle(request);
        });

        request.getProxy()
                .ifPresentOrElse(_proxy ->{
                    Proxy proxy = request.getProxy().get();
                    try {
                        this.connection = (HttpURLConnection) request.getUrl().openConnection(proxy);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }, () -> {
                    try {
                        this.connection = (HttpURLConnection) request.getUrl().openConnection();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        this.connection.setRequestMethod(request.getMethod().name());
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
        this.connection.setUseCaches(false);

        this.getConnectionTimeout().ifPresent(timeout -> {
            int timeoutInMillis = (int)timeout.toMillis();
            this.connection.setConnectTimeout(timeoutInMillis);
        });

        List<Header> requestHeaders = request.getHeaders();
        if(requestHeaders.size() > 0){
            for(Header header: requestHeaders){
                this.connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }

        this.addAuthHeaderIfProviderPresent(request, connection);
        this.connection.setRequestMethod(request.getMethod().name());

        Optional<BodyProvider<?>> bodyConverter = request.getBody();

        if(bodyConverter.isPresent()){
            OutputStream connectionOutputStream = this.connection.getOutputStream();
            BodyProvider<?> converter = bodyConverter.get();
            converter.setOutputStream(connectionOutputStream);
            converter.prepareAndCopyToStream();
            connectionOutputStream.close();
        }

        int responseStatus = this.connection.getResponseCode();

        Map<String, List<String>> headersFields = this.connection.getHeaderFields();
        List<Header> headers = this.calculateHeaders(headersFields);

        if(responseStatus >= 200 && responseStatus < 300){
            bodyHandler.setInputStream(this.connection.getInputStream());
        }
        else if(responseStatus >= 400 && responseStatus < 500){
            InputStream errorStream = this.connection.getErrorStream();
            bodyHandler.setInputStream(errorStream);
            GenericError genericError = new GenericError(responseStatus,
                    headers,
                    "Server responded with client error status: " +responseStatus,
                    ErrorType.CLIENT,
                    new String(errorStream.readAllBytes()));
            Event.operation.publish(Channels.ERROR_CHANNEL, new Communicate(genericError));
        }

        bodyHandler.setResponseStatus(getHttpStatus(responseStatus));
        bodyHandler.setHeaders(headers);

        EasyHttpResponse<T> _response = bodyHandler.getCalculatedResponse();

        this.getResponseInterceptor().ifPresent(interceptor -> {
            interceptor.handle(_response);
        });

        _response.setStatus(responseStatus);
        if(responseStatus >= 300 && responseStatus < 400){
            RedirectionHandler redirectionHandler
                    = this.getRedirectionHandler()
                    .orElseThrow(() -> {
                        GenericError genericError = new GenericError(responseStatus,
                                headers, "Server respond with redirect status: " + responseStatus + "and you did not provide redirection handler",
                                ErrorType.REDIRECT,
                                null);
                        try{
                            genericError.setServerMsg(new String(bodyHandler.getInputStream().readAllBytes()));
                        }catch (java.io.IOException e){
                            e.printStackTrace();
                        }

                        return new RedirectionUnhandled(genericError);
                    });
            try{
                redirectionHandler.modifyRequest(request, _response);
            }catch (UnsafeRedirectionException  e) {
                Event.operation.publish(Channels.ERROR_CHANNEL, e.getGenericError());
                e.printStackTrace();
            }catch (RedirectionCanNotBeHandledException e) {
                Event.operation.publish(Channels.ERROR_CHANNEL, e.getGenericError());
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

    public static class EasyHttpBuilder{
        private URL url;
        private Method method;
        private String userAgent;
        private CookieExtractor cookieExtractor;
        private AuthenticationProvider authenticationProvider;
        private Interceptor<EasyHttpResponse<?>> responseInterceptor;
        private Interceptor<EasyHttpRequest> requestIInterceptor;
        private RedirectionHandler redirectionHandler;
        private Duration connectionTimeout;
        private Map<String, Subscriber> subscribedChannels;

        public EasyHttpBuilder setURL(URL url){
            this.url = url;
            return this;
        }
        public EasyHttpBuilder setConnectionTimeout(Duration connectionTimeout){
            this.connectionTimeout = connectionTimeout;
            return this;
        }
        public EasyHttpBuilder setCookieExtractor(CookieExtractor extractor){
            this.cookieExtractor = extractor;
            return this;
        }
        public EasyHttpBuilder redirectionHandler(RedirectionHandler redirectionHandler){
            this.redirectionHandler = redirectionHandler;
            return this;
        }
        public EasyHttpBuilder setResponseInterceptor(Interceptor<EasyHttpResponse<?>> responseInterceptor){
            this.responseInterceptor = responseInterceptor;
            return this;
        }
        public EasyHttpBuilder setRequestInterceptor(Interceptor<EasyHttpRequest> requestIInterceptor){
            this.requestIInterceptor = requestIInterceptor;
            return this;
        }
        public EasyHttpBuilder setAuthenticationProvider(AuthenticationProvider authenticationProvider){
            this.authenticationProvider = authenticationProvider;
            return this;
        }
        public EasyHttpBuilder setMethod(Method method){
            this.method = method;
            return this;
        }

        public EasyHttpBuilder setUserAgent(String agent){
            this.userAgent = agent;
            return this;
        }

        public EasyHttpBuilder subscribeForChannels(Map<String, Subscriber> channelSubscriber){
            this.subscribedChannels = channelSubscriber;
            channelSubscriber.forEach((key, value) -> Event.operation.subscribe(key, value));
            return this;
        }

        public EasyHttp build() throws IOException {
            EasyHttp http = new EasyHttp();
            http.setUrl(this.url);
            http.setMethod(this.method);
            http.setUserAgent(this.userAgent);
            http.setCookieExtractor(cookieExtractor);
            http.setAuthenticationProvider(this.authenticationProvider);
            http.setRequestIInterceptor(this.requestIInterceptor);
            http.setResponseInterceptor(this.responseInterceptor);
            http.setRedirectionHandler(this.redirectionHandler);
            http.setConnectionTimeout(this.connectionTimeout);
            http.setSubscribedChannels(this.subscribedChannels);
            return http;
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

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public HttpURLConnection getConnection() {
        return connection;
    }

    public void setConnection(HttpURLConnection connection) {
        this.connection = connection;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Optional<Interceptor<EasyHttpResponse<?>>> getResponseInterceptor() {
        return Optional.ofNullable(responseInterceptor);
    }

    public void setResponseInterceptor(Interceptor<EasyHttpResponse<?>> responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
    }

    public void setRequestIInterceptor(Interceptor<EasyHttpRequest> requestIInterceptor) {
        this.requestIInterceptor = requestIInterceptor;
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
}
