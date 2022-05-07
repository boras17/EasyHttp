package Utils.simplerequest.auth;

import Headers.HttpHeader;
import Utils.simplerequest.EasyHttpRequest;

import java.util.List;

public interface AuthenticateChannel {
    default void on401Response(List<HttpHeader> httpHeaders, EasyHttpRequest request){

    }
    void beforeRequest(EasyHttpRequest request);
}
