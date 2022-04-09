import client.EasyHttp;
import exceptions.RequestObjectRequiredException;
import exceptions.ResponseHandlerRequired;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import publishsubscribe.Operation;
import redirect.GenericError;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;

public class EasyHttpTest {

    @Mock
    private Operation operation;

    @InjectMocks
    private EasyHttp easyHttp;

    private final EasyHttpRequest emptyRequest = new EasyHttpRequest.EasyHttpRequestBuilder().build();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = RequestObjectRequiredException.class)
    public void givenNullRequestShouldThrowRequestObjectRequiredException() throws IOException, RedirectionUnhandled, IllegalAccessException {
        Mockito.doNothing().when(this.operation).publish(Mockito.any(String.class), Mockito.any(GenericError.class));
        this.easyHttp.send(null, new EmptyBodyHandler());
    }

    @Test(expected = ResponseHandlerRequired.class)
    public void givenNullRequestShouldThrowResponseHandlerRequired() throws IOException, RedirectionUnhandled, IllegalAccessException {
        Mockito.doNothing().when(this.operation).publish(Mockito.any(String.class), Mockito.any(GenericError.class));
        this.easyHttp.send(emptyRequest, null);
    }
}
