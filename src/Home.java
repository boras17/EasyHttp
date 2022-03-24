import Headers.CommonHeaders;
import HttpEnums.Method;
import Parts.FilePart;
import Parts.PartType;
import auth.digestauth.HashAlgorithms;
import jsonoperations.JsonCreator;
import jsonoperations.serialization.EasySerialize;
import jsonoperations.serialization.LocalDateTimeSerializer;
import requests.easyresponse.EasyHttpResponse;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyrequest.MultipartBody;
import requests.multirpart.simplerequest.EasyHttpRequest;
import requests.multirpart.simplerequest.jsonsender.bodysenders.JsonBodySender;
import requests.multirpart.simplerequest.jsonsender.bodysenders.MultiPartBodySender;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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

    public static void main(String[] args) throws IOException, IllegalAccessException {


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
    }

}
