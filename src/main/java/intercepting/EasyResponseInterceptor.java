package intercepting;

import requests.easyresponse.EasyHttpResponse;

public interface EasyResponseInterceptor<T> extends Interceptor<EasyHttpResponse<T>>{
}
