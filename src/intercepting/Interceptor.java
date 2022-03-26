package intercepting;

import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Interceptor{
    private final BiConsumer<EasyHttpResponse, Object> responseHandler;
    private final Function<EasyHttpRequest, EasyHttpRequest> requestHandler;

    public Interceptor(UnaryOperator<EasyHttpRequest> requestHandler,
                       BiConsumer<EasyHttpResponse, Object> responseHandler) {
        this.requestHandler = requestHandler;
        this.responseHandler = responseHandler;
    }

    public  BiConsumer<EasyHttpResponse, Object> getResponseHandler() {
        return responseHandler;
    }

    public Function<EasyHttpRequest, EasyHttpRequest> getRequestHandler() {
        return requestHandler;
    }
}
