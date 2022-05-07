import Headers.HttpHeader;
import HttpEnums.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.bodyhandlers.StreamBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StringBodyHandler.class, AbstractBodyHandler.class})
public class RequestBodyProviderHandlerTests {


    @Test
    public void givenStringBodyHandlerWithJsonBodyShouldCorrectlyCalculateHeadersStatusAndResposne() throws Exception {
        StringBodyHandler stringBodyHandler = PowerMockito.spy(new StringBodyHandler());

        InputStream mockedInputStream = new ByteArrayInputStream("Hello world".getBytes(StandardCharsets.UTF_8));

        PowerMockito.doCallRealMethod()
                        .when(stringBodyHandler, "calculateBody");
        PowerMockito.when(stringBodyHandler.getResponseStatus()).thenReturn(HttpStatus.SUCCESSFUL);
        PowerMockito.when(stringBodyHandler.getHeaders()).thenReturn(List.of(new HttpHeader("Content-Type", "application/json")));
        PowerMockito.when(stringBodyHandler.getInputStream()).thenReturn(mockedInputStream);

        PowerMockito.when(stringBodyHandler.getCalculatedResponse()).thenCallRealMethod();

        EasyHttpResponse<String> response = stringBodyHandler.getCalculatedResponse();

        String calculatedBody = response.getBody();
        HttpStatus calculatedResponseStatus = response.getResponseStatus();
        List<HttpHeader> calculatedHttpHeaders = response.getResponseHeaders();

        Assertions.assertThat(calculatedBody).isEqualTo("Hello world");
        Assertions.assertThat(calculatedHttpHeaders).anyMatch(new Predicate<HttpHeader>() {
            @Override
            public boolean test(HttpHeader httpHeader) {
                return httpHeader.getKey().equals("Content-Type")
                        &&
                        httpHeader.getValue().equals("application/json");
            }
        });
        Assertions.assertThat(calculatedResponseStatus).isEqualTo(HttpStatus.SUCCESSFUL);
    }

    @Test
    public void givenStreamBodyHandlerShouldCorrectlySetStreamHeadersAndStatus() throws Exception {
        StreamBodyHandler streamBodyHandler = PowerMockito.spy(new StreamBodyHandler());

        InputStream mockedInputStream = new ByteArrayInputStream("Hello world".getBytes(StandardCharsets.UTF_8));

        PowerMockito.when(streamBodyHandler.getResponseStatus()).thenReturn(HttpStatus.SUCCESSFUL);
        PowerMockito.when(streamBodyHandler.getHeaders()).thenReturn(List.of(new HttpHeader("Content-Type", "application/json")));
        PowerMockito.when(streamBodyHandler.getInputStream()).thenReturn(mockedInputStream);

        PowerMockito.when(streamBodyHandler.getCalculatedResponse()).thenCallRealMethod();

        EasyHttpResponse<InputStream> response = streamBodyHandler.getCalculatedResponse();

        InputStream calculatedBody = response.getBody();
        HttpStatus calculatedResponseStatus = response.getResponseStatus();
        List<HttpHeader> calculatedHttpHeaders = response.getResponseHeaders();

        Assertions.assertThat(calculatedBody).matches(new Predicate<InputStream>() {
            @Override
            public boolean test(InputStream inputStream) {
                return inputStream.equals(mockedInputStream);
            }
        });
        Assertions.assertThat(calculatedHttpHeaders).anyMatch(new Predicate<HttpHeader>() {
            @Override
            public boolean test(HttpHeader httpHeader) {
                return httpHeader.getKey().equals("Content-Type")
                        &&
                        httpHeader.getValue().equals("application/json");
            }
        });
        Assertions.assertThat(calculatedResponseStatus).isEqualTo(HttpStatus.SUCCESSFUL);
    }

    @Test
    public void givenEmptyBodyHandlerShouldCorrectlySetResponseStatusAndHeader() {
        EmptyBodyHandler emptyBodyHandler = PowerMockito.spy(new EmptyBodyHandler());

        PowerMockito.when(emptyBodyHandler.getResponseStatus()).thenReturn(HttpStatus.SUCCESSFUL);
        PowerMockito.when(emptyBodyHandler.getHeaders()).thenReturn(List.of(new HttpHeader("Content-Type", "application/json")));

        PowerMockito.when(emptyBodyHandler.getCalculatedResponse()).thenCallRealMethod();

        EasyHttpResponse<Void> response = emptyBodyHandler.getCalculatedResponse();

        HttpStatus calculatedResponseStatus = response.getResponseStatus();
        List<HttpHeader> calculatedHttpHeaders = response.getResponseHeaders();

        Assertions.assertThat(calculatedHttpHeaders).anyMatch(new Predicate<HttpHeader>() {
            @Override
            public boolean test(HttpHeader httpHeader) {
                return httpHeader.getKey().equals("Content-Type")
                        &&
                        httpHeader.getValue().equals("application/json");
            }
        });
        Assertions.assertThat(calculatedResponseStatus).isEqualTo(HttpStatus.SUCCESSFUL);
    }
}
