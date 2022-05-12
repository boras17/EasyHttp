package client.clients.interceptingmodel;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ResponseInterceptors<T> implements Iterable<ResponseInterceptorWrapper<T>>, Interceptors<ResponseInterceptorWrapper<T>>{
    private List<ResponseInterceptorWrapper<T>> responseInterceptors;
    private int maxsize;
    private int offset;

    public ResponseInterceptors(List<ResponseInterceptorWrapper<T>> responseInterceptors) {
        this.maxsize = responseInterceptors.size();
        this.offset = 0;
        this.responseInterceptors = responseInterceptors;
    }

    @Override
    public Iterator<ResponseInterceptorWrapper<T>> iterator() {
        Collections.sort(this.responseInterceptors);
        return new Iterator<ResponseInterceptorWrapper<T>>() {
            @Override
            public boolean hasNext() {
                return offset < maxsize;
            }

            @Override
            public ResponseInterceptorWrapper<T> next() {
                ResponseInterceptorWrapper<T> interceptor = responseInterceptors.get(offset);
                offset+=1;
                return interceptor;
            }
        };
    }

    @Override
    public void addInterceptor(ResponseInterceptorWrapper<T> interceptor) {
        this.responseInterceptors.add(interceptor);
    }

    @Override
    public void removeInterceptor(ResponseInterceptorWrapper<T> interceptor) {
        this.responseInterceptors.remove(interceptor);
    }

    @Override
    public void addAllInterceptors(List<ResponseInterceptorWrapper<T>> interceptors) {
        this.responseInterceptors.addAll(interceptors);
    }

    @Override
    public void removeAllInterceptors(List<ResponseInterceptorWrapper<T>> interceptors) {
        this.responseInterceptors.removeAll(interceptors);
    }
}
