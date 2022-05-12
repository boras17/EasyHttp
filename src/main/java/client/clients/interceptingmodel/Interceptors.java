package client.clients.interceptingmodel;

import java.util.List;

public interface Interceptors<T> {
    void addInterceptor(T interceptor);
    void removeInterceptor(T interceptor);
    default void addAllInterceptors(List<T> interceptors){}
    default void removeAllInterceptors(List<T> interceptors){}
}
