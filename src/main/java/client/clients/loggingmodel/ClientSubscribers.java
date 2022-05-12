package client.clients.loggingmodel;

import publishsubscribe.Operation;
import publishsubscribe.communcates.notifications.GenericAppError;
import publishsubscribe.communcates.notifications.GenericHttpError;
import publishsubscribe.communcates.notifications.GenericHttpNotification;
import publishsubscribe.errorsubscriberimpl.Subscriber;

import java.util.HashMap;
import java.util.Map;

public class ClientSubscribers{

    private Map<String, Subscriber<GenericHttpError>> httpErrorChannels;
    private Map<String, Subscriber<GenericAppError>> applicationErrors;
    private Map<String, Subscriber<GenericHttpNotification>> notificationChannels;

    public ClientSubscribers(){
        this.httpErrorChannels = new HashMap<>();
        this.applicationErrors = new HashMap<>();
        this.notificationChannels = new HashMap<>();
    }

    public enum ChannelScope{
        HTTP_ERROR_CHANNELS, APPLICATION_ERRORS, NOTIFICATION_CHANNELS
    }

    public void initOperation(Operation operation){
        for(Map.Entry<String, Subscriber<GenericHttpError>> subscriber: this.httpErrorChannels.entrySet()) {
            operation.subscribe(subscriber.getKey(), subscriber.getValue());
        }
        for(Map.Entry<String, Subscriber<GenericAppError>> subscriber: this.applicationErrors.entrySet()) {
            operation.subscribe(subscriber.getKey(), subscriber.getValue());
        }
        for(Map.Entry<String, Subscriber<GenericHttpNotification>> subscriber: this.notificationChannels.entrySet()) {
            operation.subscribe(subscriber.getKey(), subscriber.getValue());
        }
    }

    public boolean checkIfSubscriberRegistered(String channelName, ChannelScope type) {
        return switch (type){
            case HTTP_ERROR_CHANNELS -> this.httpErrorChannels.containsKey(channelName);
            case APPLICATION_ERRORS -> this.applicationErrors.containsKey(channelName);
            case NOTIFICATION_CHANNELS -> this.notificationChannels.containsKey(channelName);
        };
    }

    public void registerHttpErrorChannel(String channelName, Subscriber<GenericHttpError> subscriber) {
        this.httpErrorChannels.put(channelName, subscriber);
    }
    public void registerAppErrorChannel(String channelName, Subscriber<GenericAppError> subscriber) {
        this.applicationErrors.put(channelName, subscriber);
    }
    public void registerHttpNotificationChannel(String channelName, Subscriber<GenericHttpNotification> subscriber) {
        this.notificationChannels.put(channelName, subscriber);
    }

    public void removeHttpErrorChannel(String channelName){
        this.httpErrorChannels.remove(channelName);
    }
    public void removeAppErrorChannel(String channelName){
        this.applicationErrors.remove(channelName);
    }
    public void removeHttpNotificationChannel(String channelName){
        this.notificationChannels.remove(channelName);
    }
}
