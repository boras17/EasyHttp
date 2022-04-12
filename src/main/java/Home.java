import HttpEnums.Method;
import auth.AuthenticateChannel;
import auth.AuthenticationProvider;
import auth.digestauth.DigestAuthenticationProvider;
import client.EasyHttp;
import client.EasyHttpBuilder;
import intercepting.Interceptor;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

public class Home {

    public static void main(String[] args) throws IOException, RedirectionUnhandled, IllegalAccessException, NoSuchAlgorithmException {
        EasyHttp easyHttp = new EasyHttpBuilder().build();

        EasyHttpRequest request = new EasyHttpRequest
                .EasyHttpRequestBuilder()
                .setUri(new URL("http://localhost:3333/hello"))
                .setMethod(Method.GET)
                .build();

        EasyHttpResponse<Void> response = easyHttp.send(request, new EmptyBodyHandler());

        DigestAuthenticationProvider authenticationProvider
                = new DigestAuthenticationProvider("adam","kowalski");

        authenticationProvider.setRequest(request);
        authenticationProvider.setResponse(response.getResponseHeaders());

        authenticationProvider.calculate();

        authenticationProvider.getAuthHeaders()
                .forEach(header -> {
                    System.out.println(header.getValue());
                });
    }
}
