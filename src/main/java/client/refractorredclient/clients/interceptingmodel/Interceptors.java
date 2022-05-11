package client.refractorredclient.clients.interceptingmodel;

import intercepting.Interceptor;

public interface Interceptors<T> {
    void addInterceptor(T interceptor);
    void removeInterceptor(T interceptor);
}
