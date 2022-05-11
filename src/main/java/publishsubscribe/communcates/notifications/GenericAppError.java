package publishsubscribe.communcates.notifications;

import publishsubscribe.communcates.GenericCommunicate;
import publishsubscribe.ChannelMessageType;

public class GenericAppError extends GenericCommunicate<Exception> {

    public GenericAppError(Exception exception, ChannelMessageType messageType) {
        super(exception, messageType);
    }

    @Override
    public String formatGenericCommunicate() {
            return super.getCommunicate().getMessage();
    }
}
