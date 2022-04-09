package publishsubscribertests;

import Headers.Header;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationWrapper;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import publishsubscribe.communcates.ErrorCommunicate;
import publishsubscribe.errorsubscriberimpl.ErrorChannelConfigProp;
import publishsubscribe.errorsubscriberimpl.ErrorSubscriber;
import redirect.ErrorType;
import redirect.GenericError;

import java.io.File;
import java.util.List;
import java.util.Properties;

@PrepareForTest(ErrorSubscriber.class)
@RunWith(PowerMockRunner.class)
public class ErrorSubscriberTest {


    @Test
    public void giveRedirectErrorShouldPrintMessageIntoFile() throws Exception {
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

        GenericError genericError = new GenericError(122,
                List.of(new Header("key", "value")),
                "some exception msg",
                ErrorType.REDIRECT);

        ErrorCommunicate errorCommunicate = new ErrorCommunicate(genericError);

        errorSubscriber.onRedirectErrorCommunicate(errorCommunicate);

        PowerMockito.verifyPrivate(errorSubscriber, Mockito.times(1))
                .invoke("writeError", Mockito.any(ErrorCommunicate.class),
                        Mockito.any(String.class));
        PowerMockito.verifyPrivate(errorSubscriber, Mockito.times(1))
                .invoke("writeErrorToFile", Mockito.anyString(), Mockito.anyString());
    }
}
