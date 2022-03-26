package requests.bodyhandlers;

import requests.easyresponse.EasyHttpResponse;

import java.io.IOException;

public class EmptyBodyHandler extends AbstractBodyHandler<Void>{
    @Override
    protected void calculateBody() throws IOException {

    }

    @Override
    public EasyHttpResponse<Void> getCalculatedResponse() throws IOException {
        EasyHttpResponse<Void> easyHttpResponse = new EasyHttpResponse<>();
        easyHttpResponse.setResponseStatus(super.getResponseStatus());
        easyHttpResponse.setResponseHeaders(super.getHeaders());
        return easyHttpResponse;
    }
}
