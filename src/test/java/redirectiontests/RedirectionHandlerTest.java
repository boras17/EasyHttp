package redirectiontests;

import HttpEnums.Method;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import redirect.RedirectionHandler;
import redirect.redirectexception.RedirectWithoutLocationException;
import requests.easyresponse.EasyHttpResponse;
import Utils.simplerequest.EasyHttpRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

@RunWith(PowerMockRunner.class)
@PrepareForTest(RedirectionHandler.class)
public class RedirectionHandlerTest {

    @Test
    public void givenRedirectableMethod_To_isRedirectable_ShouldReturnTrue() throws Exception {
        RedirectionHandler redirectionHandler = new RedirectionHandler(Set.of(Method.GET, Method.POST));

        RedirectionHandler spyHandler = PowerMockito.spy(redirectionHandler);

        PowerMockito.doCallRealMethod()
                .when(spyHandler, "isRedirectable", Mockito.isA(Method.class));

        boolean result = WhiteboxImpl.invokeMethod(redirectionHandler, "isRedirectable",  Method.GET);

        Assertions.assertThat(result)
                .isTrue();
    }

    @Test
    public void givenRedirectableMethod_To_isRedirectable_ShouldReturnFalse() throws Exception {
        RedirectionHandler redirectionHandler = new RedirectionHandler(Set.of(Method.GET, Method.POST));

        RedirectionHandler spyHandler = PowerMockito.spy(redirectionHandler);

        PowerMockito.doCallRealMethod()
                .when(spyHandler, "isRedirectable", Mockito.isA(Method.class));

        boolean result = WhiteboxImpl.invokeMethod(redirectionHandler, "isRedirectable",  Method.HEAD);

        Assertions.assertThat(result)
                .isFalse();
    }

    @Test(expected = MalformedURLException.class)
    public void givenIncorrectURL_To_createLocationURL_ShouldThrowMalformedURLException() throws Exception {
        RedirectionHandler redirectionHandler = new RedirectionHandler(Collections.emptySet());
        RedirectionHandler spyHandler = PowerMockito.spy(redirectionHandler);

        PowerMockito.doCallRealMethod()
                .when(spyHandler,
                        "createLocationURL",
                        Mockito.isA(URL.class), Mockito.anyString());

        URL requestURL = new URL("htts://www.google.com/home");
        String resourceLocation = "some/resource";
        WhiteboxImpl
                .invokeMethod(spyHandler,"createLocationURL", requestURL, resourceLocation);
    }

    @Test
    public void givenNotAbsoluteLocationShouldConstructCorrectURL() throws Exception {
        RedirectionHandler redirectionHandler = new RedirectionHandler(Collections.emptySet());
        RedirectionHandler spyRedirectionHandler = PowerMockito.spy(redirectionHandler);

        URL baseURL = new URL("https://www.google.com/home");

        PowerMockito.doCallRealMethod()
                .when(spyRedirectionHandler,
                        "createLocationURL",
                        Mockito.isA(URL.class),
                        Mockito.anyString());
        String location = "/second/home";
        URL result = WhiteboxImpl.invokeMethod(spyRedirectionHandler, "createLocationURL", baseURL, location);
        URL expected = baseURL.toURI().resolve(location).toURL();

        Assertions.assertThat(result)
                .is(new Condition<>(){
                    @Override
                    public boolean matches(URL value) {
                        return value.equals(expected);
                    }
                });
    }

    @Test(expected = RedirectWithoutLocationException.class)
    public void givenRedirectRequestWithoutLocationHeaderShouldThrowRedirectWithoutLocationException() throws Exception {
        RedirectionHandler redirectionHandler = new RedirectionHandler(Collections.emptySet());
        RedirectionHandler spyRedirectionHandler = PowerMockito.spy(redirectionHandler);

        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder().build();
        EasyHttpResponse<Void> emptyResponse = new EasyHttpResponse<>();
        emptyResponse.setResponseHeaders(Collections.emptyList());

        PowerMockito.doCallRealMethod()
                .when(spyRedirectionHandler,
                        "modifyRequest",
                        Mockito.isA(EasyHttpRequest.class),
                        Mockito.isA(EasyHttpResponse.class));
        WhiteboxImpl
                .invokeMethod(spyRedirectionHandler, "modifyRequest", request, emptyResponse);
    }
}
