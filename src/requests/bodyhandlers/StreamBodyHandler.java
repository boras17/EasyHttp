package requests.bodyhandlers;

import requests.easyresponse.EasyHttpResponse;

import java.io.IOException;
import java.io.InputStream;

public class StreamBodyHandler extends AbstractBodyHandler<InputStream> {

    @Override
    public EasyHttpResponse<InputStream> getCalculatedResponse() throws IOException {
        EasyHttpResponse<InputStream> easyHttpResponse = new EasyHttpResponse<>();
        easyHttpResponse.setResponseStatus(super.getResponseStatus());
        easyHttpResponse.setBody(super.getInputStream());
        easyHttpResponse.setResponseHeaders(super.getHeaders());
        return easyHttpResponse;
    }
}
