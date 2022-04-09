import client.EasyHttp;
import client.EasyHttpBuilder;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;

public class Home {
    public static void main(String[] args) throws IOException, RedirectionUnhandled, IllegalAccessException {
        EasyHttp easyHttp = new EasyHttpBuilder().build();
        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder().build();
        EasyHttpResponse<String> response = easyHttp.send(request, new StringBodyHandler());
    }
}
