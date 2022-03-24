package requests.multirpart.simplerequest.jsonsender.bodysenders;


import requests.multirpart.simplerequest.jsonsender.BodyConverter;

import java.io.IOException;
import java.net.HttpURLConnection;

public class TextBodySender extends BodyConverter<String> {

    protected TextBodySender(String request) {
        super(request);
    }

    @Override
    public void prepareAndCopyToStream() throws IllegalAccessException, IOException {

    }

}
