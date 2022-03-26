package requests.bodyhandlers;

import requests.easyresponse.EasyHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class StringBodyHandler extends AbstractBodyHandler<String>{

    public StringBodyHandler(){

    }

    @Override
    protected void calculateBody() throws IOException {
        StringBuilder stringContent = new StringBuilder();
        InputStream inputStream = super.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line = null;
        while((line = bufferedReader.readLine()) != null){
            stringContent.append(line);
        }

        super.setBody(stringContent.toString());
    }

    @Override
    public EasyHttpResponse<String> getCalculatedResponse() throws IOException {
        this.calculateBody();

        EasyHttpResponse<String> easyStringResponse = new EasyHttpResponse<>();

        easyStringResponse.setBody(super.getBody());
        easyStringResponse.setResponseStatus(super.getResponseStatus());
        easyStringResponse.setResponseHeaders(super.getHeaders());

        return easyStringResponse;
    }
}