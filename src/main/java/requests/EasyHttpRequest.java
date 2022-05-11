package requests;

import headers.HttpHeader;
import httpenums.HttpMethod;
import requests.bodyproviders.BodyProvider;

import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EasyHttpRequest {
    private URL url;
    private final HttpMethod httpMethod;
    private final BodyProvider<?> body;
    private final List<HttpHeader> httpHeaders;
    private Proxy proxy;

    public EasyHttpRequest(URL url, HttpMethod httpMethod, BodyProvider<?> body, List<HttpHeader> httpHeaders, Proxy proxy) {
        this.url = url;
        this.httpMethod = httpMethod;
        this.body = body;
        this.httpHeaders = httpHeaders;
        this.proxy = proxy;
    }

    public static class EasyHttpRequestBuilder{
        private URL url;
        private HttpMethod httpMethod;
        private BodyProvider<?> body;
        private final List<HttpHeader> httpHeaders = new ArrayList<>();
        private Proxy proxy;

        public EasyHttpRequestBuilder setUri(URL url) {
            this.url = url;
            return this;
        }

        public EasyHttpRequestBuilder addHeader(HttpHeader httpHeader){
            this.httpHeaders.add(httpHeader);
            return this;
        }

        public EasyHttpRequestBuilder addHeaders(List<HttpHeader> httpHeaders){
            this.httpHeaders.addAll(httpHeaders);
            return this;
        }

        public EasyHttpRequestBuilder setProxy(Proxy proxy){
            this.proxy = proxy;

            return this;
        }

        public EasyHttpRequestBuilder setMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public EasyHttpRequestBuilder setBodyProvider(BodyProvider<?> body) {
            this.body = body;
            return this;
        }

        public EasyHttpRequest build(){
            return new EasyHttpRequest(url, httpMethod, body, httpHeaders, proxy);
        }
    }

    public URL getUrl() {
        return url;
    }

    public List<HttpHeader> getHeaders() {
        return httpHeaders;
    }

    public HttpMethod getMethod() {
        return httpMethod;
    }

    public Optional<BodyProvider<?>> getBody() {
        return Optional.ofNullable(body);
    }

    public Optional<Proxy> getProxy() {
        return Optional.ofNullable(proxy);
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
