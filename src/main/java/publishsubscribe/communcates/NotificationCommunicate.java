package publishsubscribe.communcates;

import publishsubscribe.GenericCommunicate;
import redirect.GenericNotification;

public class NotificationCommunicate extends GenericCommunicate<GenericNotification> {
    public NotificationCommunicate(GenericNotification genericNotification){
        super(genericNotification);
    }
}
