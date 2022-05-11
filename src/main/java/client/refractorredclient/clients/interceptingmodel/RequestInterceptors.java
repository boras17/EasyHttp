package client.refractorredclient.clients.interceptingmodel;

import intercepting.EasyRequestInterceptor;

import java.util.Iterator;
import java.util.List;

public class RequestInterceptors implements Iterable<EasyRequestInterceptor>, Interceptors<EasyRequestInterceptor>{
    private List<EasyRequestInterceptor> requestInterceptorList;
    private int maxsize;
    private int offset;

    public RequestInterceptors(List<EasyRequestInterceptor> requestInterceptors) {
        this.maxsize = requestInterceptors.size();
        this.offset = 0;
        this.requestInterceptorList = requestInterceptors;
    }



    @Override
    public Iterator<EasyRequestInterceptor> iterator() {
        return new Iterator<EasyRequestInterceptor>() {
            @Override
            public boolean hasNext() {
                return offset < maxsize;
            }

            @Override
            public EasyRequestInterceptor next() {
                EasyRequestInterceptor easyRequestInterceptor = requestInterceptorList.get(offset);
                offset+=1;
                return easyRequestInterceptor;
            }
        };
    }


    @Override
    public void addInterceptor(EasyRequestInterceptor interceptor) {
        this.requestInterceptorList.add(interceptor);
    }

    @Override
    public void removeInterceptor(EasyRequestInterceptor interceptor) {
        this.requestInterceptorList.remove(interceptor);
    }
}
