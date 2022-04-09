package interceptingtests.httpurlconnections;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ClientErrorConnection extends HttpURLConnection {
    private final int responseStatus;
     public ClientErrorConnection(int responseStatus) throws MalformedURLException {
        super(new URL("http://empty/asd"));
        this.responseStatus = responseStatus;
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
        return this.responseStatus;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public InputStream getErrorStream() {
        return new ByteArrayInputStream("An client error occurred".getBytes(StandardCharsets.UTF_8));
    }
}
