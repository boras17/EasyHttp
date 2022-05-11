package client.refractorredclient.clients.interceptingmodel;

import intercepting.EasyResponseInterceptor;

public class ResponseInterceptorWrapper<T> implements Comparable<ResponseInterceptorWrapper<T>> {
    private EasyResponseInterceptor<T> responseInterceptor;
    private int responseInterceptorOrder;

    public ResponseInterceptorWrapper(EasyResponseInterceptor<T> responseInterceptor, int responseInterceptorOrder) {
        this.responseInterceptor = responseInterceptor;
        this.responseInterceptorOrder = responseInterceptorOrder;
    }

    @Override
    public int compareTo(ResponseInterceptorWrapper<T> o) {
        return this.responseInterceptorOrder - o.getResponseInterceptorOrder();
    }

    public EasyResponseInterceptor<T> getResponseInterceptor() {
        return responseInterceptor;
    }

    public void setResponseInterceptor(EasyResponseInterceptor<T> responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
    }

    public int getResponseInterceptorOrder() {
        return responseInterceptorOrder;
    }

    public void setResponseInterceptorOrder(int responseInterceptorOrder) {
        this.responseInterceptorOrder = responseInterceptorOrder;
    }
}