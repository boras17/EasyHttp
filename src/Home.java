import Headers.Header;
import HttpEnums.Method;
import redirect.RedirectSafety;
import redirect.RedirectionHandler;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import javax.print.attribute.standard.PresentationDirection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class Home {
    public static void main(String[] args) throws IOException, IllegalAccessException, RedirectionUnhandled {
        EasyHttp easyHttp
                = new EasyHttp.EasyHttpBuilder()
                .build();
        EasyHttpRequest easyHttpRequest = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setMethod(Method.GET)
                .setUri(new URL("https://62423599d126926d0c4f01c1.mockapi.io/redirection"))
                .build();
        EasyHttpResponse<String> response = easyHttp.send(easyHttpRequest, new StringBodyHandler());
    }
}
