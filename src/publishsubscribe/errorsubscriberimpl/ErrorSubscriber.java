package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.communcates.ErrorCommunicate;
import redirect.GenericError;

import java.nio.file.Path;

public class ErrorSubscriber extends Subscriber{

    private Path path;

    public ErrorSubscriber(Path path){
        super();
    }

    @Override
    public void onRedirectErrorCommunicate(ErrorCommunicate message) {
        GenericError genericError = message.getCommunicate();
        // TODO implementation
    }

    @Override
    public void onClientErrorCommunicate(ErrorCommunicate errorCommunicate) {

    }

    @Override
    public void onServerErrorCommunicate(ErrorCommunicate errorCommunicate) {

    }
}
