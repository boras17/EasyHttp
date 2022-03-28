import Headers.Header;
import HttpEnums.Method;
import redirect.RedirectSafety;
import redirect.RedirectionHandler;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import javax.print.attribute.standard.PresentationDirection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;

public class Home {
    public static void main(String[] args) throws IOException, IllegalAccessException {
        EasyHttp easyHttp
                = new EasyHttp.EasyHttpBuilder()
                .build();
        EasyHttpRequest easyHttpRequest = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setMethod(Method.GET)
                .setUri(new URL("https://62423599d126926d0c4f01c1.mockapi.io/redirection"))
                .build();
        EasyHttpResponse<String> response = easyHttp.send(easyHttpRequest, new StringBodyHandler());

        response.setStatus(HttpURLConnection.HTTP_MOVED_PERM);
        response.getResponseHeaders().add(new Header("location", "https://www.google.com"));

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        RedirectionHandler redirectionHandler = new RedirectionHandler(RedirectSafety.UN_SAFE);
        boolean re = redirectionHandler.checkIfCanBeRedirected(response, easyHttpRequest);
        redirectionHandler.modifyRequest(easyHttpRequest, response);
        EasyHttpResponse<String> response2 = easyHttp.send(easyHttpRequest, new StringBodyHandler());
        System.out.println(response2.getBody());
    }
}
