package publishsubscribertests;

import Headers.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import publishsubscribe.Channels;
import publishsubscribe.GenericCommunicate;
import publishsubscribe.Operation;
import publishsubscribe.communcates.ErrorCommunicate;
import publishsubscribe.errorsubscriberimpl.ErrorSubscriber;
import publishsubscribe.errorsubscriberimpl.Subscriber;
import redirect.ErrorType;
import redirect.GenericError;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@PrepareForTest({ErrorSubscriber.class, Operation.class})
@RunWith(PowerMockRunner.class)
public class ErrorSubscriberTest {

    private GenericError genericError = new GenericError(122,
            List.of(new Header("key", "value")),
            "some exception msg",
    ErrorType.REDIRECT);

    @Test
    public void giveRedirectErrorShouldInvokeMethods() throws Exception {
        ErrorSubscriber errorSubscriber = PowerMockito.mock(ErrorSubscriber.class);
        File mockedFile = PowerMockito.mock(File.class);
        Mockito.when(mockedFile.exists()).thenReturn(true);


        PowerMockito.when(errorSubscriber,
                "extractFile",
                Mockito.anyString())
                .thenReturn(mockedFile);

        PowerMockito.when(errorSubscriber,"writeError",
                Mockito.any(ErrorCommunicate.class), Mockito.anyString())
                .thenCallRealMethod();

        PowerMockito.doCallRealMethod()
                .when(errorSubscriber)
                .onRedirectErrorCommunicate(Mockito.any(ErrorCommunicate.class));

        ErrorCommunicate errorCommunicate = new ErrorCommunicate(genericError);

        errorSubscriber.onRedirectErrorCommunicate(errorCommunicate);

        PowerMockito.verifyPrivate(errorSubscriber, Mockito.times(1))
                .invoke("writeError", Mockito.any(ErrorCommunicate.class),
                        Mockito.any(String.class));
        PowerMockito.verifyPrivate(errorSubscriber, Mockito.times(1))
                .invoke("writeErrorToFile", Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void publishErrorsTest() throws Exception {

        ErrorSubscriber errorSubscriber = PowerMockito.mock(ErrorSubscriber.class);
        ConcurrentHashMap<String, WeakReference<Object>> channels = Mockito.spy(new ConcurrentHashMap<>());
        Operation mockedOperation = PowerMockito.spy(new Operation(channels));

        PowerMockito.doCallRealMethod()
                        .when(mockedOperation).subscribe(Mockito.anyString(), Mockito.any(Subscriber.class));
        PowerMockito.doCallRealMethod()
                .when(mockedOperation).publish(Mockito.anyString(),
                        Mockito.isA(GenericCommunicate.class));
        PowerMockito.doCallRealMethod()
                .when(mockedOperation, "publishErrors", Mockito.any(), Mockito.any(GenericCommunicate.class));

        // -- subscribe should invoke put on channelsMap
        mockedOperation.subscribe(Channels.CLIENT_ERROR_CHANNEL, errorSubscriber);
        // -- verify invocations
        Mockito.verify(channels, Mockito.times(1))
                .put(Mockito.eq(Channels.CLIENT_ERROR_CHANNEL), Mockito.any());
        // publish method with error subscriber should invoke publish errors method

        mockedOperation.publish(Channels.CLIENT_ERROR_CHANNEL, genericError);
        PowerMockito.verifyPrivate(mockedOperation,Mockito.times(1))
                .invoke("publishErrors", Mockito.any(), Mockito.any(GenericCommunicate.class));
        // -- and devliverErrors should invoke deliverMessage
        PowerMockito.verifyPrivate(mockedOperation, Mockito.times(1)).invoke(
                "deliverMessage",
                Mockito.any(Subscriber.class),
                Mockito.any(Method.class), Mockito.any(GenericCommunicate.class));

    }
}
