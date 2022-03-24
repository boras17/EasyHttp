import Headers.Header;
import HttpEnums.HttpStatus;
import HttpEnums.Method;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.cookies.CookieExtractor;
import requests.easyrequest.MultipartBody;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;
import requests.multirpart.simplerequest.jsonsender.bodysenders.JsonBodySender;
import requests.multirpart.simplerequest.jsonsender.bodysenders.MultiPartBodySender;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EasyHttp {
    private URL url;
    private Method method;
    private HttpURLConnection connection;
    private String userAgent;
    private CookieExtractor cookieExtractor;

    public void send(HttpRequest request){
        if(this.cookieExtractor != null){
            cookieExtractor.setCookies(this.connection);
        }
    }

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

    public <T> EasyHttpResponse<T> send(requests.multirpart.simplerequest.EasyHttpRequest request,
                                 AbstractBodyHandler<T> bodyHandler)
            throws IOException, IllegalAccessException {
        this.connection = (HttpURLConnection) request.getUrl().openConnection();
        this.connection.setRequestMethod(request.getMethod().name());
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
        this.connection.setUseCaches(false);

        List<Header> requestHeaders = request.getHeaders();

        for(Header header: requestHeaders){
            this.connection.addRequestProperty(header.getKey(), header.getValue());
        }

        this.connection.setRequestMethod(request.getMethod().name());

        OutputStream connectionOutputStream = this.connection.getOutputStream();
        request.getBody().setOutputStream(connectionOutputStream);
        request.getBody().prepareAndCopyToStream();

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
        return bodyHandler.getCalculatedResponse();
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

    public static class MikoHTTPBuilder{
        private URL url;
        private Method method;
        private String userAgent;
        private CookieExtractor cookieExtractor;

        public MikoHTTPBuilder setURL(URL url){
            this.url = url;
            return this;
        }

        public MikoHTTPBuilder setCookieExtractor(CookieExtractor extractor){
            this.cookieExtractor = extractor;
            return this;
        }

        public MikoHTTPBuilder setMethod(Method method){
            this.method = method;
            return this;
        }

        public MikoHTTPBuilder setUserAgent(String agent){
            this.userAgent = agent;
            return this;
        }

        public EasyHttp build() throws IOException {
            EasyHttp http = new EasyHttp();
            http.setUrl(this.url);
            http.setMethod(this.method);
            http.setUserAgent(this.userAgent);
            http.setCookieExtractor(cookieExtractor);
            return http;
        }

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
