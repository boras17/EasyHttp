package client.clients.interceptingmodel;

public class InterceptingConfigurer<ResponseType>{
    private ResponseInterceptors<ResponseType> responseInterceptors;
    private RequestInterceptors requestInterceptors;

    public ResponseInterceptors<ResponseType> getResponseInterceptors() {
        return responseInterceptors;
    }

    public void setResponseInterceptors(ResponseInterceptors<ResponseType> responseInterceptors) {
        this.responseInterceptors = responseInterceptors;
    }

    public RequestInterceptors getRequestInterceptors() {
        return requestInterceptors;
    }

    public void setRequestInterceptors(RequestInterceptors requestInterceptors) {
        this.requestInterceptors = requestInterceptors;
    }
}
