package requests.multirpart.simplerequest.jsonsender.bodysenders;

import requests.multirpart.simplerequest.jsonsender.BodyProvider;
import jsonoperations.JsonCreator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class JsonBodyProvider extends BodyProvider<Object> {

    public JsonBodyProvider(Object request) {
        super(request);
    }

    @Override
    public void prepareAndCopyToStream() throws IllegalAccessException, IOException {
        OutputStream outputStream = super.getOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream), true);
        JsonCreator jsonCreator = new JsonCreator();
        jsonCreator.generateJson(super.getRequest());

        String json = jsonCreator.getJson();
        printWriter.write(json);
        printWriter.flush();
    }

}
