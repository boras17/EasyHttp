package client;

import org.junit.runner.RunWith;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Optional;

public class ConnectionInitializr {

    public ConnectionInitializr() {

    }

    public HttpURLConnection openConnection(EasyHttpRequest request) throws IOException {
        HttpURLConnection connection = null;
        Optional<Proxy> proxyOptional = request.getProxy();
        URL url = request.getUrl();
        if(proxyOptional.isPresent()) {
            connection = (HttpURLConnection)url.openConnection(proxyOptional.get());
        }else{
            connection = (HttpURLConnection)url.openConnection();
        }
        connection.setRequestMethod(request.getMethod().name());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        return connection;
    }

}
