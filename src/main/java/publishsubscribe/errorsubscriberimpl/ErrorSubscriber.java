package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.annotations.OnClientError;
import publishsubscribe.annotations.OnRedirectError;
import publishsubscribe.annotations.OnServerError;
import publishsubscribe.communcates.ErrorCommunicate;
import redirect.GenericError;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ErrorSubscriber extends Subscriber<ErrorCommunicate>{

    private Properties properties;

    public ErrorSubscriber(Properties properties){
        super();
        this.properties = properties;
    }

    private void writeToFile(ErrorCommunicate communicate, String errorFile){
        GenericError error = communicate.getCommunicate();
        String content_to_write = GenericError.formattedGenericError(error);
        String path_str = this.properties.getProperty(errorFile);

        Path file = Paths.get(path_str);

        try{
            PrintWriter writer = new PrintWriter(file.toFile());
            writer.write(content_to_write);
            writer.flush();
        }catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    @OnRedirectError
    @Override
    public void onRedirectErrorCommunicate(ErrorCommunicate message) {
        this.writeToFile(message, ErrorChannelConfigProp.REDIRECT_ERROR_FILE);
    }

    @OnClientError
    @Override
    public void onClientErrorCommunicate(ErrorCommunicate communicate) {
        this.writeToFile(communicate, ErrorChannelConfigProp.CLIENT_ERROR_FILE);
    }

    @OnServerError
    @Override
    public void onServerErrorCommunicate(ErrorCommunicate communicate) {
        this.writeToFile(communicate, ErrorChannelConfigProp.SERVER_ERROR_FILE);
    }
}
