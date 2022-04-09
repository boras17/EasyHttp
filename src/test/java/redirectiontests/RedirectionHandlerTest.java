package redirectiontests;

import HttpEnums.Method;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import redirect.RedirectionHandler;

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
    public void givenNotAbsoluteLocationShouldConstructCorrectURL() {
        RedirectionHandler redirectionHandler = new RedirectionHandler(Collections.emptySet());
        RedirectionHandler spyRedirectionHandler = PowerMockito.spy(redirectionHandler);

    }
}
