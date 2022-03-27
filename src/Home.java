import HttpEnums.Method;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.URL;

public class Home {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        EasyHttp easyHttp
                = new EasyHttp.EasyHttpBuilder()
                .setRequestInterceptor(request -> {
                    System.out.println("request: " + request.getUrl());
                })
                .setResponseInterceptor(easyHttpResponse -> {
                    System.out.println(easyHttpResponse.getBody());
                })
                .build();
        EasyHttpRequest easyHttpRequest = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setMethod(Method.GET)
                .setUri(new URL("https://jsonplaceholder.typicode.com/todos/1"))
                .build();
        EasyHttpResponse<String> response = easyHttp.send(easyHttpRequest, new StringBodyHandler());

    }
}
