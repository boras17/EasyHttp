package requests.multirpart.simplerequest.jsonsender.bodysenders;


import requests.multirpart.simplerequest.jsonsender.BodyProvider;

import java.io.IOException;

public class TextBodyProvider extends BodyProvider<String> {

    protected TextBodyProvider(String request) {
        super(request);
    }

    @Override
    public void prepareAndCopyToStream() throws IllegalAccessException, IOException {

    }

}
