package requests.multirpart.simplerequest.jsonsender.bodysenders;


import jsonoperations.JsonCreator;
import requests.multirpart.simplerequest.jsonsender.BodyProvider;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class TextBodyProvider extends BodyProvider<String> {

    public TextBodyProvider(String request) {
        super(request);
    }

    @Override
    public void prepareAndCopyToStream() throws IllegalAccessException, IOException {
        OutputStream outputStream = super.getOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream), true);
        printWriter.write(super.getRequest());
        printWriter.flush();
    }

}
