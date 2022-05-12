package interceptingtests;

import client.EasyHttpClient;
import client.clients.InterceptableClientDecorator;
import client.clients.interceptingmodel.RequestInterceptors;
import client.clients.interceptingmodel.ResponseInterceptorWrapper;
import client.clients.interceptingmodel.ResponseInterceptors;
import headers.HttpHeader;
import intercepting.EasyRequestInterceptor;
import intercepting.EasyResponseInterceptor;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.bodyhandlers.StringBodyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EasyInterceptorTests {

    @Test
    public void givenJsonDataInterceptorShouldUpperJsonData() throws Exception {
        EasyHttpResponse<String> mockedResponse = new EasyHttpResponse<>();
        mockedResponse.setBody("""
                {
                  "userId": 1,
                  "id": 1,
                  "title": "delectus aut autem",
                  "completed": false
                }
                """);
        EasyHttpClient client = Mockito.mock(EasyHttpClient.class);

        List<ResponseInterceptorWrapper<String>> wrappers = new ArrayList<>();

        wrappers.add(new ResponseInterceptorWrapper<>(stringEasyHttpResponse -> {
            String body = stringEasyHttpResponse.getBody();
            stringEasyHttpResponse.setBody(body.toUpperCase(Locale.ROOT));
        },1));

        ResponseInterceptors<String> responseInterceptors = new ResponseInterceptors<>(wrappers);
        EasyHttpClient interceptableClient = new InterceptableClientDecorator(client, responseInterceptors);

        // when
        Mockito.when(client.send(Mockito.any(EasyHttpRequest.class), Mockito.any(AbstractBodyHandler.class)))
                .thenReturn(mockedResponse);

        //then
        EasyHttpResponse<String> response = interceptableClient.send(new EasyHttpRequest.EasyHttpRequestBuilder().build(), new StringBodyHandler());
        String expectedResponseBody =
                        """
                        {
                          "USERID": 1,
                          "ID": 1,
                          "TITLE": "DELECTUS AUT AUTEM",
                          "COMPLETED": FALSE
                        }
                        """;
        Assertions.assertEquals(expectedResponseBody, response.getBody());
    }

    @Test
    public void givenTwoInterceptorsShouldInvokeAccordinglyWithOrder() {
        EasyHttpClient client = PowerMockito.mock(EasyHttpClient.class);

        EasyResponseInterceptor<String> first = PowerMockito.mock(EasyResponseInterceptor.class);
        EasyResponseInterceptor<String> second = PowerMockito.mock(EasyResponseInterceptor.class);

        ResponseInterceptorWrapper<String> secondInterceptor = new ResponseInterceptorWrapper<>(second,2);
        ResponseInterceptorWrapper<String> firstInterceptor = new ResponseInterceptorWrapper<>(first,1);

        ResponseInterceptors<String> responseInterceptors = new ResponseInterceptors<>(Arrays.asList(secondInterceptor, firstInterceptor));
        InterceptableClientDecorator interceptableClientDecorator = new InterceptableClientDecorator(client, responseInterceptors);

        InOrder inOrder = Mockito.inOrder(first, second);

        interceptableClientDecorator.send(new EasyHttpRequest.EasyHttpRequestBuilder().build(), new StringBodyHandler());

        inOrder.verify(first).handle(Mockito.any());
        inOrder.verify(second).handle(Mockito.any());
    }

    @Test
    public void givenRequestInterceptorShouldAddJwtHeaderToRequest() {
        EasyHttpResponse<String> mockedResponse = new EasyHttpResponse<>();
        mockedResponse.setBody("""
                {
                  "userId": 1,
                  "id": 1,
                  "title": "delectus aut autem",
                  "completed": false
                }
                """);
        EasyHttpClient client = PowerMockito.mock(EasyHttpClient.class);

        EasyRequestInterceptor jwtInterceptor = request -> {
            HttpHeader jwtHeader = new HttpHeader();
            jwtHeader.setKey("Authorization");
            jwtHeader.setValue("Bearer someJwtCode");
            request.getHeaders().add(jwtHeader);
        };
        RequestInterceptors requestInterceptors = new RequestInterceptors(Arrays.asList(jwtInterceptor));
        InterceptableClientDecorator interceptableClientDecorator = new InterceptableClientDecorator(client, requestInterceptors);

        PowerMockito.when(client.send(Mockito.any(), Mockito.any(StringBodyHandler.class))).thenReturn(mockedResponse);
        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder().build();

        interceptableClientDecorator.send(request, new StringBodyHandler());

        List<HttpHeader> headers = request.getHeaders();
        int headersSize = headers.size();
        Assertions.assertEquals(1,headersSize);
        HttpHeader addedHeader = headers.get(0);
        org.assertj.core.api.Assertions.assertThat(addedHeader)
                .has(new Condition<>(){
                    @Override
                    public boolean matches(HttpHeader value) {
                        return value.getKey().equals("Authorization") && value.getValue().equals("Bearer someJwtCode");
                    }
                });
    }
}
