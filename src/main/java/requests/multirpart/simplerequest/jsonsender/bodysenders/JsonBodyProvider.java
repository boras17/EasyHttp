package requests.multirpart.simplerequest.jsonsender.bodysenders;

import requests.multirpart.simplerequest.jsonsender.BodyProvider;
import jsonoperations.JsonCreator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

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
        super.setInputStream(json.getBytes(StandardCharsets.UTF_8));
        printWriter.write(json);
        printWriter.flush();
    }

}
