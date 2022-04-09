package requests.bodyhandlers;

import requests.easyresponse.EasyHttpResponse;

import java.io.IOException;

public class EmptyBodyHandler extends AbstractBodyHandler<Void>{

    @Override
    public EasyHttpResponse<Void> getCalculatedResponse() {
        EasyHttpResponse<Void> easyHttpResponse = new EasyHttpResponse<>();
        easyHttpResponse.setResponseStatus(super.getResponseStatus());
        easyHttpResponse.setResponseHeaders(super.getHeaders());
        return easyHttpResponse;
    }
}
