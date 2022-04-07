package cookieextractortests;

import Headers.Header;
import client.EasyHttp;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.cookies.Cookie;
import requests.cookies.CookieExtractor;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

public class CookieExtractorTest {
    private CookieExtractor cookieExtractor;


    @Before
    public void init() {
        this.cookieExtractor = new CookieExtractor();
    }
    @Test
    public void givenResponseWithSetCookieWithExpiresShouldReturnCookieObject() throws IOException, RedirectionUnhandled,
            IllegalAccessException {
        EasyHttpResponse<String> mockedResponse = new EasyHttpResponse<>();
        mockedResponse.setStatus(200);

        Header cookie = new Header();
        cookie.setKey("Set-Cookie");
        cookie.setValue("id=a3fWa; Expires=Wed, 21 Oct 2015 07:28:00 GMT");

        mockedResponse.setResponseHeaders(List.of(cookie));

        cookieExtractor.setCookies(mockedResponse);

        List<Cookie> cookies = cookieExtractor.getCookies();
        Assertions.assertEquals(1, cookies.size());
        Cookie first_cookie = cookies.get(0);

        String cookieName = first_cookie.getCookieName(),
                cookieValue = first_cookie.getCookieValue();

        LocalDateTime cookieExpiresAt = first_cookie.getExpiresAt();


        Assertions.assertAll(
        () -> {
            Assertions.assertEquals("a3fWa", cookieValue);
        }, () -> {
            Assertions.assertEquals("id", cookieName);
        }, () ->{
            Assertions.assertEquals(DayOfWeek.WEDNESDAY, cookieExpiresAt.getDayOfWeek());
        }, () -> {
            Assertions.assertEquals(21, cookieExpiresAt.getDayOfMonth());
        }, () -> {
            Assertions.assertEquals(7, cookieExpiresAt.getHour());
        }, () -> {
            Assertions.assertEquals(28, cookieExpiresAt.getMinute());
        }, () -> {
            Assertions.assertEquals(0, cookieExpiresAt.getSecond());
        });
    }

    @Test
    public void givenCookieHeaderWithHttpOnlyAndSecuredFlagShouldReturnParsedCookieObject() {
        EasyHttpResponse<Void> responseWitCookie = new EasyHttpResponse<>();

        Header cookieHeader = new Header();
        cookieHeader.setKey("Set-Cookie");
        cookieHeader.setValue("GAPS=1:4gDZvK5g:o5fM52umoiFpi0So;HttpOnly;Secure");

        responseWitCookie.setResponseHeaders(List.of(cookieHeader));

        this.cookieExtractor.setCookies(responseWitCookie);

        List<Cookie> cookies = this.cookieExtractor.getCookies();
        Cookie cookie = cookies.get(0);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(1, cookies.size());
        }, () -> {
            Assertions.assertTrue(cookie.isHttpOnly());
        }, () -> {
            Assertions.assertTrue(cookie.isSecured());
        });
    }

    @Test
    public void givenCookieWithDomainPathShouldReturnParsedCookieObject() {
        Header cookieHeader = new Header();
        cookieHeader.setKey("Set-Cookie");
        cookieHeader.setValue("qwerty=219ffwef9w0f; Domain=somecompany.co.uk");

        EasyHttpResponse<Void> response = new EasyHttpResponse<>();
        response.setResponseHeaders(List.of(cookieHeader));

        this.cookieExtractor.setCookies(response);

        List<Cookie> extractedCookies = this.cookieExtractor.getCookies();
        Assertions.assertEquals(1, extractedCookies.size());
        Cookie cookie = extractedCookies.get(0);
        Assertions.assertEquals("somecompany.co.uk", cookie.getDomain());
    }

    @Test
    public void givenCookieWithMaxAgeShouldReturnParsedCookie() {
        Header cookieHeader = new Header();
        cookieHeader.setKey("Set-Cookie");
        cookieHeader.setValue("id=a3fWa; Max-Age=2592000");

        EasyHttpResponse<Void> response = new EasyHttpResponse<>();
        response.setResponseHeaders(List.of(cookieHeader));

        this.cookieExtractor.setCookies(response);

        List<Cookie> cookies = this.cookieExtractor.getCookies();
        Assertions.assertEquals(1, cookies.size());

        Cookie cookie = cookies.get(0);
        Assertions.assertEquals(2592000, cookie.getMaxAge());
    }
}
