package interceptingtests;


import Headers.HttpHeader;
import client.ConnectionInitializr;
import client.EasyHttp;
import intercepting.EasyRequestInterceptor;
import intercepting.EasyResponseInterceptor;
import interceptingtests.httpurlconnections.DumbConnection;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import Utils.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RunWith(MockitoJUnitRunner.class)
public class EasyInterceptorTests {

    @Mock
    private ConnectionInitializr connectionInitializr;

    @InjectMocks
    private EasyHttp easyHttp;

    private EasyHttpRequest emptyRequest;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        this.emptyRequest = new EasyHttpRequest.EasyHttpRequestBuilder().build();
    }

    @Test
    public void da() throws IOException,
                            RedirectionUnhandled,
                            IllegalAccessException {

        Mockito.when(connectionInitializr.openConnection(Mockito.any())).thenReturn(new DumbConnection());



        easyHttp.addResponseInterceptor(new EasyResponseInterceptor<Object>() {
            @Override
            public void handle(EasyHttpResponse<Object> stringEasyHttpResponse) {
                String dateBody = (String) stringEasyHttpResponse.getBody();
                LocalDateTime dateTime =
                        LocalDateTime.parse(dateBody, DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm"));
                stringEasyHttpResponse.setBody(dateTime);
            }
        },1);
        easyHttp.addResponseInterceptor(new EasyResponseInterceptor<Object>() {

            @Override
            public void handle(EasyHttpResponse<Object> objectEasyHttpResponse) {
                Object body = objectEasyHttpResponse.getBody();
                if (body instanceof LocalDateTime) {
                    LocalDateTime localDateTime = (LocalDateTime) body;
                    int getYear = localDateTime.getYear();
                    objectEasyHttpResponse.setBody(String.valueOf(getYear));
                }
            }
        },2 );

        EasyHttpResponse<String> response = easyHttp.send(emptyRequest, new StringBodyHandler());

        Assertions.assertEquals("1984", response.getBody());
    }

    @Test
    public void givenRequestInterceptorToEasyHttpShouldAddJwtHeaderToRequest() throws IOException,
                                                                                      RedirectionUnhandled,
                                                                                      IllegalAccessException {
        Mockito.when(connectionInitializr.openConnection(Mockito.any())).thenReturn(new DumbConnection());
        HttpHeader authHttpHeader = new HttpHeader();
        authHttpHeader.setKey("Authorization");
        authHttpHeader.setValue("Bearer knsivbnsodifyba98sdhv9as8dvxkcj");

        EasyRequestInterceptor jwtHeaderInterceptor = new EasyRequestInterceptor() {
            @Override
            public void handle(EasyHttpRequest request) {
                request.getHeaders().add(authHttpHeader);
            }
        };

        this.easyHttp.setRequestInterceptor(jwtHeaderInterceptor);

        this.easyHttp.send(this.emptyRequest, new StringBodyHandler());

        Assertions.assertEquals(1,this.emptyRequest.getHeaders().size());
        HttpHeader addedHttpHeader = this.emptyRequest.getHeaders().get(0);
        org.assertj.core.api.Assertions.assertThat(addedHttpHeader).has(new Condition<>(){
            @Override
            public boolean matches(HttpHeader httpHeader) {
                return httpHeader.getKey().equals(authHttpHeader.getKey())
                        &&
                       httpHeader.getValue().equals(authHttpHeader.getValue());
            }
        });
    }

}
