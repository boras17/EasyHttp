package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.annotations.redirectannotations.OnRedirectNotification;
import publishsubscribe.communcates.ErrorCommunicate;
import redirect.GenericError;
import redirect.GenericNotification;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class NotificationSubscriber extends Subscriber<GenericNotification>{

    private Properties properties;

    public NotificationSubscriber(Properties properties){
        super();
        this.properties = properties;
    }

    private void writeToFile(GenericNotification communicate){
        GenericNotification notification = communicate.getCommunicate();

        String content_to_write = GenericNotification.formattedGenericNotification(notification);
        String path_str = this.properties.getProperty(ErrorChannelConfigProp.REDIRECT_NOTIFICATION_FILE);

        Path file = Paths.get(path_str);

        try{
            PrintWriter writer = new PrintWriter(file.toFile());
            writer.write(content_to_write);
            writer.flush();
        }catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    @OnRedirectNotification
    @Override
    public void onRedirectErrorCommunicate(GenericNotification message) {
        this.writeToFile(message);
    }

}
