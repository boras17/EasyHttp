package requests.bodyproviders;

import jsonoperations.JsonCreator;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class JsonBodyProvider extends BodyProvider<Object> {


    public JsonBodyProvider(Object request) {
        super(request);
    }

    private String getJson() throws IllegalAccessException {
        JsonCreator jsonCreator = new JsonCreator();
        jsonCreator.generateJson(super.getRequest());
        return jsonCreator.getJson();
    }

    @Override
    public void prepareAndCopyToStream() throws IllegalAccessException {
        OutputStream outputStream = super.getOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream), true);
        printWriter.write(getJson());
        printWriter.flush();
    }

}
