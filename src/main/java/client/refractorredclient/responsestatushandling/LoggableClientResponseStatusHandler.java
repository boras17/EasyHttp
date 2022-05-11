package client.refractorredclient.responsestatushandling;

import client.refractorredclient.clients.loggingmodel.ClientSubscribers;
import client.refractorredclient.connectiondata.ConnectionData;
import client.refractorredclient.clients.LoggableClient;
import publishsubscribe.*;
import publishsubscribe.communcates.notifications.GenericAppError;
import publishsubscribe.communcates.notifications.GenericHttpError;
import publishsubscribe.communcates.notifications.GenericHttpNotification;
import publishsubscribe.constants.Channels;
import publishsubscribe.ChannelMessageType;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.AbstractBodyHandler;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;

public class LoggableClientResponseStatusHandler extends ResponseStatusHandler{


    public LoggableClientResponseStatusHandler(LoggableClient client) {
        super(client);
    }

    @Override
    public <T> void handle2xxResponse(ConnectionData<T> connectionData) {
        try {
            super.default2xxStatusLogic(connectionData.getConnection(), connectionData.getBodyHandler());
            boolean requestNotificationChannelPresent = this.getClientSubscribers().checkIfSubscriberRegistered(Channels.REQUEST_NOTIFICATION, ClientSubscribers.ChannelScope.NOTIFICATION_CHANNELS);
            if(requestNotificationChannelPresent){
                this.getOperation().publish(Channels.REQUEST_NOTIFICATION, new GenericHttpNotification(LocalDateTime.now(), "Server successfully respond", connectionData.getResponse().getResponseHeaders(), connectionData.getRequest().getUrl().toString(),ChannelMessageType.NOTIFICATION));
            }
        }
        catch(IOException e) {
            boolean appErrorChannelSubscriberPresent = this.getClientSubscribers().checkIfSubscriberRegistered(Channels.APP_ERROR_CHANNEL, ClientSubscribers.ChannelScope.APPLICATION_ERRORS);
            if(appErrorChannelSubscriberPresent){
                GenericAppError genericApplicationError = new GenericAppError(e, ChannelMessageType.APP);
                this.getOperation().publish(Channels.APP_ERROR_CHANNEL, genericApplicationError);
            }else{
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public <T> void handle3xxResponse(ConnectionData<T> connectionData) throws MalformedURLException, UnsafeRedirectionException, RedirectionCanNotBeHandledException {
        EasyHttpRequest easyHttpRequest = connectionData.getRequest();
        EasyHttpResponse<T> response = connectionData.getResponse();
        super.defaultRedirectionLogic(easyHttpRequest, this.getClient(), response);

        boolean redirectionNotificationSubscriberPresent = this.getClientSubscribers().checkIfSubscriberRegistered(Channels.REDIRECT_NOTIFICATION, ClientSubscribers.ChannelScope.NOTIFICATION_CHANNELS);
        if(redirectionNotificationSubscriberPresent){
            this.getOperation().publish(Channels.REDIRECT_NOTIFICATION, new GenericHttpNotification(LocalDateTime.now(),
                    "An redirection occurred",
                    response.getResponseHeaders(),
                    easyHttpRequest.getUrl().toString(),ChannelMessageType.REDIRECT));
        }

    }

    @Override
    public <T> void handle4xxResponse(ConnectionData<T> connectionData) {
        AbstractBodyHandler<T> bodyHandler = connectionData.getBodyHandler();
        EasyHttpResponse<T> response = connectionData.getResponse();
        super.default4xxResponseStatusLogic(connectionData.getConnection(), bodyHandler);
        try{
             boolean clientErrorChannelSubscriberPresent = this.getClientSubscribers().checkIfSubscriberRegistered(Channels.APP_ERROR_CHANNEL, ClientSubscribers.ChannelScope.HTTP_ERROR_CHANNELS);
            if(clientErrorChannelSubscriberPresent){
                int responseStatus = response.getStatus();
                GenericHttpError genericHttpError = new GenericHttpError(responseStatus,
                        response.getResponseHeaders(),
                        ChannelMessageType.SERVER,
                        "Server responded with client error status: " +responseStatus,
                        bodyHandler.getInputStream() == null ? "No message from server": new String(bodyHandler.getInputStream().readAllBytes()));
                this.getOperation().publish(Channels.CLIENT_ERROR_CHANNEL, genericHttpError);
            }
        }catch (IOException e) {
            GenericAppError genericApplicationError = new GenericAppError(e, ChannelMessageType.APP);
            boolean errorChannelSubscriberPresent = this.getClientSubscribers().checkIfSubscriberRegistered(Channels.APP_ERROR_CHANNEL, ClientSubscribers.ChannelScope.APPLICATION_ERRORS);
            if(errorChannelSubscriberPresent){
                this.getOperation().publish(Channels.APP_ERROR_CHANNEL, genericApplicationError);
            }else{
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public <T> void handle5xxResponse(ConnectionData<T> connectionData) {
        AbstractBodyHandler<T> bodyHandler = connectionData.getBodyHandler();
        EasyHttpResponse<T> response = connectionData.getResponse();
        super.default5xxResponseStatusLogic(connectionData.getConnection(), bodyHandler);

        int responseStatus = response.getStatus();
        try{
            boolean serverErrorChannel = this.getClientSubscribers().checkIfSubscriberRegistered(Channels.SERVER_ERROR_CHANNEL, ClientSubscribers.ChannelScope.HTTP_ERROR_CHANNELS);
            if(serverErrorChannel){
                GenericHttpError genericHttpError = new GenericHttpError(responseStatus,
                        response.getResponseHeaders(),
                        ChannelMessageType.SERVER,
                        "Server responded with server error status: " +responseStatus,
                        new String(bodyHandler.getInputStream().readAllBytes()));
                this.getOperation().publish(Channels.SERVER_ERROR_CHANNEL, genericHttpError);
            }

        }catch (IOException e){
            boolean applicationErrorChannelSubscriberPresent = this.getClientSubscribers().checkIfSubscriberRegistered(Channels.APP_ERROR_CHANNEL, ClientSubscribers.ChannelScope.APPLICATION_ERRORS);
            if(applicationErrorChannelSubscriberPresent){
                this.getOperation().publish(Channels.APP_ERROR_CHANNEL, new GenericAppError(e, ChannelMessageType.APP));
            }else{
                throw new RuntimeException(e);
            }
        }
    }

    private LoggableClient convertClientToLoggableClient() {
        return (LoggableClient) super.getClient();
    }

    private Operation getOperation(){
        return this.convertClientToLoggableClient().operation();
    }

    private ClientSubscribers getClientSubscribers() {
        return this.convertClientToLoggableClient().subscribers();
    }
}
