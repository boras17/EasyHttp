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

public class DefaultSubscriber implements Subscriber<GenericHttpNotification>{

    private Properties properties;

    public DefaultSubscriber(Properties properties){
        super();
        this.properties = properties;
    }

    private Path extractFile(String errorFile) {
        String path_str = this.properties.getProperty(errorFile);
        return Paths.get(path_str);
    }

    private void writeErrorToFile(String errorFile, String errorCommunicate) {
        try{
            Files.writeString(this.extractFile(errorFile), errorCommunicate.concat(System.lineSeparator()), StandardOpenOption.APPEND);
        } catch (IOException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    private void writeError(String communicate, String errorFile){
        this.writeErrorToFile(errorFile, communicate);
    }


    @OnNotification
    public void onNotification(GenericHttpNotification notification) {
        this.writeError(notification.formatGenericCommunicate(), ErrorChannelConfigProp.REQUEST_NOTIFICATION_FILE);

    }
}
