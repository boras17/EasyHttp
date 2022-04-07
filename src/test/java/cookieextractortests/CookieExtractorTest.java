package cookieextractortests;

import Headers.Header;
import client.EasyHttp;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.cookies.CookieExtractor;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.List;

public class CookieExtractorTest {

    @Test
    public void givenMockedHttpResponseWithSetCookieShouldExtractCookieValues() throws IOException, RedirectionUnhandled,
            IllegalAccessException {
        EasyHttp easyHttp = Mockito.mock(EasyHttp.class);
        CookieExtractor cookieExtractor = new CookieExtractor();

        easyHttp.setCookieExtractor(cookieExtractor);

        EasyHttpResponse<String> mockedResponse = new EasyHttpResponse<>();
        mockedResponse.setStatus(200);
        mockedResponse.setResponseHeaders(List.of(new Header("Set-cookie", "id=a3fWa; Expires=Wed, 21 Oct 2015 07:28:00 GMT")));

        Mockito.when(easyHttp.send(Mockito.any(EasyHttpRequest.class),
                Mockito.any(AbstractBodyHandler.class)))
                .thenReturn(mockedResponse);

        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder().build();
        EasyHttpResponse<String> response = easyHttp.send(request,
                new StringBodyHandler());


    }
}
