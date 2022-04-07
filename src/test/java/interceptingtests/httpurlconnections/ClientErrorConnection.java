package interceptingtests.httpurlconnections;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClientErrorConnection extends HttpURLConnection {
     public ClientErrorConnection() throws MalformedURLException {
        super(new URL("http://empty/asd"));
    }

    @Override
    public void disconnect() {

    }

    @Override
    public boolean usingProxy() {
        return false;
    }

    @Override
    public void connect() throws IOException {

    }

    @Override
    public int getResponseCode() throws IOException {
        return 400;
    }

    @Override
    public InputStream getErrorStream() {
        return new ByteArrayInputStream("An client error occurred".getBytes(StandardCharsets.UTF_8));
    }
}
