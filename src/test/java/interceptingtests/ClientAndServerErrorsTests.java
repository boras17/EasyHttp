package interceptingtests;

import client.ConnectionInitializr;
import client.EasyHttp;
import interceptingtests.httpurlconnections.ClientErrorConnection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import publishsubscribe.Channels;
import publishsubscribe.GenericCommunicate;
import publishsubscribe.Operation;
import publishsubscribe.communcates.Communicate;
import publishsubscribe.errorsubscriberimpl.ErrorSubscriber;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class ClientAndServerErrorsTests {

    @Mock
    private ConnectionInitializr connectionInitializr;

    @Mock
    private Operation operation;

    @Mock
    private ErrorSubscriber errorSubscriber;

    @InjectMocks
    private EasyHttp easyHttp;

    private final EasyHttpRequest emptyRequest = new EasyHttpRequest.EasyHttpRequestBuilder()
            .build();

    @Before
    public void initMock() throws IOException {
        MockitoAnnotations.initMocks(this);
        Mockito.when(this.connectionInitializr.openConnection(Mockito.any(EasyHttpRequest.class)))
                .thenReturn(new ClientErrorConnection());
        Mockito.doNothing().when(this.errorSubscriber).onClientErrorCommunicate(Mockito.any(Communicate.class));
        Mockito.doNothing().when(this.errorSubscriber).onRedirectErrorCommunicate(Mockito.any(Communicate.class));
    }

    @Test
    public void givenConnectionWithClientErrorShouldInvoke() throws IOException, RedirectionUnhandled, IllegalAccessException {
        Properties errorSubscriberEmptyProperties = new Properties();
        easyHttp.setSubscribedChannels(Map.of(Channels.ERROR_CHANNEL, new ErrorSubscriber(errorSubscriberEmptyProperties)));

        easyHttp.send(emptyRequest, new EmptyBodyHandler());
        Mockito.verify(operation, Mockito.times(1))
                .publish(Mockito.any(String.class), Mockito.any(GenericCommunicate.class));
    }
}
