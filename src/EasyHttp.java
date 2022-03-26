import Headers.Header;
import HttpEnums.HttpStatus;
import HttpEnums.Method;
import auth.AuthenticationProvider;
import intercepting.Interceptor;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.cookies.CookieExtractor;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;
import requests.multirpart.simplerequest.jsonsender.BodyProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
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
    private Interceptor interceptor;

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
            } catch (IOException | IllegalAccessException e) {
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
                System.out.println("auth header -----");
                System.out.println(header.getValue());
                System.out.println(header.getKey());
                connection.setRequestProperty(header.getKey(),header.getValue());
            }
        }
    }

    public <T> EasyHttpResponse<T> send(requests.multirpart.simplerequest.EasyHttpRequest _request,
                                 AbstractBodyHandler<T> bodyHandler)
            throws IOException, IllegalAccessException {

        EasyHttpRequest request = interceptor.getRequestHandler().apply(_request);

        if(request.getProxy().isPresent()){
            Proxy proxy = request.getProxy().get();
            System.out.println("has proxy");
            this.connection = (HttpURLConnection) request.getUrl().openConnection(proxy);
        }
        else{
            this.connection = (HttpURLConnection) request.getUrl().openConnection();
        }
        this.connection.setRequestMethod(request.getMethod().name());
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
        this.connection.setUseCaches(false);

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

        if(responseStatus >= 200 && responseStatus < 400){

            bodyHandler.setInputStream(this.connection.getInputStream());
        }
        else{
            bodyHandler.setInputStream(this.connection.getErrorStream());
        }

        bodyHandler.setResponseStatus(getHttpStatus(responseStatus));

        Map<String, List<String>> headersFields = this.connection.getHeaderFields();
        List<Header> headers = this.calculateHeaders(headersFields);

        bodyHandler.setHeaders(headers);

        EasyHttpResponse<T> _response = bodyHandler.getCalculatedResponse();

        this.interceptor.getResponseHandler().accept(_response, _response.getBody());
        return _response; //bodyHandler.getCalculatedResponse();
    }

    private List<Header> calculateHeaders(Map<String, List<String>> headersFields) {
        return headersFields
                .entrySet()
                .stream()
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
        private Interceptor interceptor;

        public EasyHttpBuilder setURL(URL url){
            this.url = url;
            return this;
        }

        public EasyHttpBuilder setCookieExtractor(CookieExtractor extractor){
            this.cookieExtractor = extractor;
            return this;
        }

        public EasyHttpBuilder setAuthenticationProvider(AuthenticationProvider authenticationProvider){
            this.authenticationProvider = authenticationProvider;
            return this;
        }
        public EasyHttpBuilder interceptor(Interceptor interceptor){
            this.interceptor = interceptor;
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

        public EasyHttp build() throws IOException {
            EasyHttp http = new EasyHttp();
            http.setUrl(this.url);
            http.setMethod(this.method);
            http.setUserAgent(this.userAgent);
            http.setCookieExtractor(cookieExtractor);
            http.setAuthenticationProvider(this.authenticationProvider);
            http.setInterceptor(this.interceptor);
            return http;
        }

    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public void setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }


    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public CookieExtractor getCookieExtractor() {
        return cookieExtractor;
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
}
