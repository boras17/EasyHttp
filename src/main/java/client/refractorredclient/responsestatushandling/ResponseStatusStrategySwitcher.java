package client.refractorredclient.responsestatushandling;

import client.refractorredclient.connectiondata.ConnectionData;
import httpenums.HttpStatus;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;

import java.net.MalformedURLException;

public class ResponseStatusStrategySwitcher<T>{
    private ResponseStatusHandler responseStatusHandler;
    private ConnectionData<T> connectionData;

    public ResponseStatusStrategySwitcher(ResponseStatusHandler responseStatusHandler, ConnectionData<T> connectionData){
        this.responseStatusHandler = responseStatusHandler;
        this.connectionData = connectionData;
    }

    public void handleResponseStatus(int responseStatus) throws MalformedURLException, UnsafeRedirectionException, RedirectionCanNotBeHandledException {
        HttpStatus status = HttpStatus.getStatus(responseStatus);
        switch (status){
            case SUCCESSFUL -> {
                this.responseStatusHandler.handle2xxResponse(this.connectionData);
            }
            case SERVER_ERROR -> {
                this.responseStatusHandler.handle5xxResponse(this.connectionData);
            }
            case REDIRECTED -> {
                this.responseStatusHandler.handle3xxResponse(this.connectionData);
            }
            case CLIENT_ERROR -> {
                this.responseStatusHandler.handle4xxResponse(this.connectionData);
            }
        }
    }
}
