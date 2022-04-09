package interceptingtests;

import HttpEnums.Method;
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
import publishsubscribe.errorsubscriberimpl.ErrorSubscriber;
import publishsubscribe.errorsubscriberimpl.Subscriber;
import redirect.RedirectionHandler;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.RedirectionUnhandled;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class ClientAndServerErrorsTests {

    @Mock
    private ConnectionInitializr connectionInitializr;

    @Mock
    private Operation operation;

    @InjectMocks
    private EasyHttp easyHttp;

    private final EasyHttpRequest emptyRequest = new EasyHttpRequest.EasyHttpRequestBuilder()
            .build();

    @Before
    public void initMock() throws IOException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void givenConnectionWithClientErrorShouldInvoke() throws IOException,
            RedirectionUnhandled,
            IllegalAccessException {

        Mockito.when(connectionInitializr.openConnection(Mockito.any(EasyHttpRequest.class)))
                .thenReturn(new ClientErrorConnection(400));

        Properties errorSubscriberEmptyProperties = new Properties();
        Subscriber errorSubscriber = new ErrorSubscriber(errorSubscriberEmptyProperties);

        easyHttp.setSubscribedChannels(Map.of(Channels.CLIENT_ERROR_CHANNEL, errorSubscriber));
        easyHttp.send(emptyRequest, new EmptyBodyHandler());

        Mockito.verify(operation, Mockito.times(1))
                .publish(Mockito.any(String.class), Mockito.any(GenericCommunicate.class));
        Mockito.verify(operation, Mockito.times(1))
                .subscribe(Channels.CLIENT_ERROR_CHANNEL, errorSubscriber);
    }

    @Test
    public void givenConnectionWithServerErrorShouldInvoke() throws IOException, RedirectionUnhandled, IllegalAccessException {
        Mockito.when(connectionInitializr.openConnection(Mockito.any(EasyHttpRequest.class)))
                .thenReturn(new ClientErrorConnection(500));

        Properties errorSubscriberProperties = new Properties();
        Subscriber subscriber = new ErrorSubscriber(errorSubscriberProperties);

        easyHttp.setSubscribedChannels(Map.of(Channels.SERVER_ERROR_CHANNEL, subscriber));
        easyHttp.send(emptyRequest, new EmptyBodyHandler());

        Mockito.verify(operation, Mockito.times(1))
                .publish(Mockito.any(String.class), Mockito.any(GenericCommunicate.class));
        Mockito.verify(operation, Mockito.times(1))
                .subscribe(Channels.SERVER_ERROR_CHANNEL, subscriber);
    }



    @Test
    public void givenConnectionWithRedirectErrorShouldInvoke() throws IOException, RedirectionUnhandled, IllegalAccessException, UnsafeRedirectionException, RedirectionCanNotBeHandledException {
        Mockito.when(connectionInitializr.openConnection(Mockito.any(EasyHttpRequest.class)))
                .thenReturn(new ClientErrorConnection(300));

        Properties errorSubscriberProperties = new Properties();
        Subscriber subscriber = new ErrorSubscriber(errorSubscriberProperties);
        RedirectionHandler redirectionHandler = Mockito.mock(RedirectionHandler.class);

        Mockito.doNothing().when(redirectionHandler).modifyRequest(Mockito.any(EasyHttpRequest.class), Mockito.any(EasyHttpResponse.class));

        easyHttp.setRedirectionHandler(redirectionHandler);
        easyHttp.setSubscribedChannels(Map.of(Channels.REDIRECT_ERROR_CHANNEL, subscriber));
        easyHttp.send(emptyRequest, new EmptyBodyHandler());

        Mockito.verify(operation, Mockito.times(1))
                .publish(Mockito.any(String.class), Mockito.any(GenericCommunicate.class));
        Mockito.verify(operation, Mockito.times(1))
                .subscribe(Channels.REDIRECT_ERROR_CHANNEL, subscriber);
    }


}
