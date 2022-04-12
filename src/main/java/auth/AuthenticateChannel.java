package auth;

import Headers.Header;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.util.List;

public interface AuthenticateChannel {
    default void on401Response(List<Header> headers, EasyHttpRequest request){

    }
    void beforeRequest(EasyHttpRequest request);
}
