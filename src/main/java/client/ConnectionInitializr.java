package client;

import requests.EasyHttpRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Optional;

public class ConnectionInitializr {

    public ConnectionInitializr() {

    }

    public HttpURLConnection openConnection(EasyHttpRequest request) {
        HttpURLConnection connection = null;
        Optional<Proxy> proxyOptional = request.getProxy();
        URL url = request.getUrl();
        try{
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
        }catch (IOException e){
            throw new RuntimeException(e);
        }

    }

}
