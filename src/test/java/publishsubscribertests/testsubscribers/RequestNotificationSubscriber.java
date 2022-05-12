package publishsubscribertests.testsubscribers;

import publishsubscribe.annotations.OnNotification;
import publishsubscribe.communcates.notifications.GenericHttpNotification;
import publishsubscribe.constants.ErrorChannelConfigProp;
import publishsubscribe.errorsubscriberimpl.Subscriber;

import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class RequestNotificationSubscriber extends Subscriber<GenericHttpNotification> {
    public RequestNotificationSubscriber(Properties properties) {
        super(properties);
    }

    @OnNotification
    public void onNotification(GenericHttpNotification genericHttpNotification) {
        super.writeError(genericHttpNotification.formatGenericCommunicate(), this.getProperties().getProperty(ErrorChannelConfigProp.REQUEST_NOTIFICATION_FILE), getProperties(), StandardOpenOption.WRITE);
    }
}
