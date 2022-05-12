package client.responsestatushandling;

import client.connectiondata.ConnectionData;
import client.EasyHttpClient;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;

import java.io.IOException;
import java.net.MalformedURLException;

public class DefaultClientResponseStatusHandler extends ResponseStatusHandler{

    public DefaultClientResponseStatusHandler(EasyHttpClient easyHttpClient) {
        super(easyHttpClient);
    }

    @Override
    public <T> void handle2xxResponse(ConnectionData<T> connectionData) {
        try{
            super.default2xxStatusLogic(connectionData.getConnection(), connectionData.getBodyHandler());
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void handle3xxResponse(ConnectionData<T> connectionData) throws MalformedURLException, UnsafeRedirectionException, RedirectionCanNotBeHandledException {
        super.defaultRedirectionLogic(connectionData.getRequest(), super.getClient(), connectionData.getResponse());
    }

    @Override
    public <T> void handle4xxResponse(ConnectionData<T> connectionData) {
        super.default4xxResponseStatusLogic(connectionData.getConnection(), connectionData.getBodyHandler());
    }

    @Override
    public <T> void handle5xxResponse(ConnectionData<T> connectionData) {
        super.default5xxResponseStatusLogic(connectionData.getConnection(), connectionData.getBodyHandler());
    }


}
