package intercepting;

import requests.EasyHttpResponse;

public interface EasyResponseInterceptor<T> extends Interceptor<EasyHttpResponse<T>>{
}
