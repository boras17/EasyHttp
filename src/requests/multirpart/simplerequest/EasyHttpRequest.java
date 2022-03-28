package requests.multirpart.simplerequest;

import Headers.Header;
import HttpEnums.Method;
import requests.multirpart.simplerequest.jsonsender.BodyProvider;

import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EasyHttpRequest {
    private URL url;
    private final Method method;
    private final BodyProvider<?> body;
    private final List<Header> headers;
    private Proxy proxy;

    public EasyHttpRequest(URL url, Method method, BodyProvider<?> body, List<Header> headers, Proxy proxy) {
        this.url = url;
        this.method = method;
        this.body = body;
        this.headers = headers;
        this.proxy = proxy;
    }

    public static class EasyHttpRequestBuilder{
        private URL url;
        private Method method;
        private BodyProvider<?> body;
        private final List<Header> headers = new ArrayList<>();
        private Proxy proxy;

        public EasyHttpRequestBuilder setUri(URL url) {
            this.url = url;
            return this;
        }

        public EasyHttpRequestBuilder addHeader(Header header){
            this.headers.add(header);
            return this;
        }

        public EasyHttpRequestBuilder addHeaders(List<Header> headers){
            this.headers.addAll(headers);
            return this;
        }

        public EasyHttpRequestBuilder setProxy(Proxy proxy){
            this.proxy = proxy;

            return this;
        }

        public EasyHttpRequestBuilder setMethod(Method method) {
            this.method = method;
            return this;
        }

        public EasyHttpRequestBuilder setBodyProvider(BodyProvider<?> body) {
            this.body = body;
            return this;
        }

        public EasyHttpRequest build(){
            return new EasyHttpRequest(url, method, body, headers, proxy);
        }
    }

    public URL getUrl() {
        return url;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public Method getMethod() {
        return method;
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
