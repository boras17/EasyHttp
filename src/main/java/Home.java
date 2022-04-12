import HttpEnums.Method;
import auth.AuthenticateChannel;
import auth.AuthenticationProvider;
import auth.digestauth.DigestAuthenticationProvider;
import auth.digestauth.DigestResponse;
import client.EasyHttp;
import client.EasyHttpBuilder;
import intercepting.EasyRequestInterceptor;
import intercepting.EasyResponseInterceptor;
import intercepting.Interceptor;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class Home {

    public static void main(String[] args) throws IOException, RedirectionUnhandled, IllegalAccessException, NoSuchAlgorithmException {
        DigestAuthenticationProvider authenticationProvider = new DigestAuthenticationProvider("adam", "password");

        EasyHttp easyHttp = new EasyHttpBuilder()
                .setAuthenticationProvider(authenticationProvider)
                .build();

        EasyHttpRequest request = new EasyHttpRequest
                .EasyHttpRequestBuilder()
                .setUri(new URL("http://localhost:3333/hello"))
                .setMethod(Method.GET)
                .build();

        easyHttp.addResponseInterceptor(new EasyResponseInterceptor<String>() {
            @Override
            public void handle(EasyHttpResponse<String> stringEasyHttpResponse) {
                int status = stringEasyHttpResponse.getStatus();
                    if(status == 401){
                        authenticationProvider.on401Response(stringEasyHttpResponse.getResponseHeaders(), request);
                    }
                }
            }
        ,1);



        EasyHttpResponse<String> response = easyHttp.send(request, new StringBodyHandler());

        EasyHttpResponse<String> respon = easyHttp.send(request, new StringBodyHandler());
        request.getHeaders()
                .forEach(h -> {
                    System.out.println(h.getValue());
                });


    }
}
