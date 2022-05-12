package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.annotations.OnAppError;
import publishsubscribe.annotations.OnNotification;
import publishsubscribe.communcates.notifications.GenericHttpNotification;
import publishsubscribe.constants.ErrorChannelConfigProp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class DefaultSubscriber extends Subscriber<GenericHttpNotification>{

    public DefaultSubscriber(Properties properties){
        super(properties);
    }

    @OnNotification
    public void onNotification(GenericHttpNotification notification) {
        this.writeError(notification.formatGenericCommunicate(), ErrorChannelConfigProp.REQUEST_NOTIFICATION_FILE, super.getProperties(), StandardOpenOption.APPEND);
    }
}
