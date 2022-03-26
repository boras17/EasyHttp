import Headers.Header;
import HttpEnums.Method;
import intercepting.Interceptor;
import jsonoperations.serialization.EasySerialize;
import jsonoperations.serialization.LocalDateTimeSerializer;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyrequest.MultipartBody;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;
import requests.multirpart.simplerequest.jsonsender.bodysenders.MultipartBodyProvider;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.function.UnaryOperator;

public class Home {

    public static class doIt{
        private String toId;

        public doIt(String toId){
            this.toId = toId;
        }
    }

    public static class PersonHome{
        private String address;
        private doIt doIt = new doIt("hello");
        public PersonHome(String address){
            this.address = address;
        }
    }

    public static class Person{
        String name;
        String surname;
        @EasySerialize(use = LocalDateTimeSerializer.class)
        LocalDateTime birth = LocalDateTime.now();

        public Person(String name, String surname){
            this.name = name;
            this.surname = surname;
        }
    }

    public static void main(String[] args) throws IOException, IllegalAccessException, NoSuchAlgorithmException {


        /*
        MultipartBody multipart = new MultipartBody.MultiPartRequestBuilder()
                .addPart(new FilePart(new File("plik.txt"),"party"))
                .setPartType(PartType.FILE)
                .encoding(StandardCharsets.UTF_8.name())
                .build();

        MultiPartBodySender multiPartBodySender = new MultiPartBodySender(multipart);

        JsonBodySender jsonBodySender = new JsonBodySender(new Person("adam", "kowalski"));

        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setMethod(Method.POST)
                .setBodyConverter(jsonBodySender)
                .addHeader(CommonHeaders.APPLICATION_JSON_HEADER.getHeader())
                .setUri(new URL("http://localhost:3232/cookies"))
                .build();

        mikoHTTP.sendAsync(request, new StringBodyHandler()).thenAccept(response -> {
            System.out.println(response.getBody());
        }).join();

        */

        // Function<EasyHttpRequest, EasyHttpRequest> handleRequest;
        // Function<EasyHttpResponse<T>, EasyHttpResponse<T>> handleResponse;

        UnaryOperator<String> unaryOperator = (e) -> {
            return e.concat("");
        };
        unaryOperator.apply(")");


        EasyHttp client = new EasyHttp.EasyHttpBuilder()
                .interceptor(new Interceptor(request -> {
                    System.out.println("requestMethod: " + request.getMethod());
                    return request;
                },  (response, payload) -> {
                    System.out.println("response payload" + payload);
                }))
                .build();


        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setUri(new URL("https://jsonplaceholder.typicode.com/posts/1"))
                .setMethod(Method.GET)
                .setBodyProvider(new MultipartBodyProvider(new MultipartBody.MultiPartBodyBuilder()

                        .build()))
                .setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("178.212.54.137",8080)))
                .addHeader(new Header("Content-Type", "application/json"))
                .build();

        EasyHttpResponse<String> response = client.send(request, new StringBodyHandler());

    }

}
