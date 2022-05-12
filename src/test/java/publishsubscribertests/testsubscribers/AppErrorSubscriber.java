package publishsubscribertests.testsubscribers;

import publishsubscribe.annotations.OnAppError;
import publishsubscribe.communcates.notifications.GenericAppError;
import publishsubscribe.constants.ErrorChannelConfigProp;
import publishsubscribe.errorsubscriberimpl.Subscriber;

import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class AppErrorSubscriber extends Subscriber<GenericAppError> {

    public AppErrorSubscriber(Properties properties) {
        super(properties);
    }

    @OnAppError
    public void onAppError(GenericAppError applicationError) {
        super.writeError(applicationError.formatGenericCommunicate(), ErrorChannelConfigProp.APP_ERROR_FILE, super.getProperties(), StandardOpenOption.WRITE);
    }

}
