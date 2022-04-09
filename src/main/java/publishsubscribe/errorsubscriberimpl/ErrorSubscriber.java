package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.annotations.OnClientError;
import publishsubscribe.annotations.OnRedirectError;
import publishsubscribe.annotations.OnServerError;
import publishsubscribe.communcates.ErrorCommunicate;
import redirect.GenericError;

import java.io.File;
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

    private File extractFile(String errorFile) {
        System.out.println(errorFile);
        String path_str = this.properties.getProperty(errorFile);
        Path file = Paths.get(path_str);
        return file.toFile();
    }

    private void writeErrorToFile(String errorFile, String errorCommunicate) {
        try{
            PrintWriter writer = new PrintWriter(extractFile(errorFile));
            writer.write(errorCommunicate);
            writer.flush();
        }catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    private void writeError(ErrorCommunicate communicate, String errorFile){
        GenericError error = communicate.getCommunicate();
        String content_to_write = GenericError.formattedGenericError(error);
        this.writeErrorToFile(errorFile, content_to_write);
    }

    @OnRedirectError
    @Override
    public void onRedirectErrorCommunicate(ErrorCommunicate message) {
        this.writeError(message, ErrorChannelConfigProp.REDIRECT_ERROR_FILE);
    }

    @OnClientError
    @Override
    public void onClientErrorCommunicate(ErrorCommunicate communicate) {
        this.writeError(communicate, ErrorChannelConfigProp.CLIENT_ERROR_FILE);
    }

    @OnServerError
    @Override
    public void onServerErrorCommunicate(ErrorCommunicate communicate) {
        this.writeError(communicate, ErrorChannelConfigProp.SERVER_ERROR_FILE);
    }
}
