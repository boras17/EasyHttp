package interceptingtests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DumbConnection extends HttpURLConnection {

    public DumbConnection() throws MalformedURLException {
        super(new URL("http://localhost:2323/any"));
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
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream("1984-11-12 11:45".getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public int getResponseCode() throws IOException {
        return 200;
    }
}
