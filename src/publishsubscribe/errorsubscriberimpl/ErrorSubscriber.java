package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.annotations.OnClientError;
import publishsubscribe.annotations.OnRedirectError;
import publishsubscribe.annotations.OnServerError;
import publishsubscribe.communcates.ErrorCommunicate;

import java.nio.file.Path;

public class ErrorSubscriber extends Subscriber{

    private Path path;

    public ErrorSubscriber(Path path){
        super();
    }

    @OnRedirectError
    @Override
    public void onRedirectErrorCommunicate(ErrorCommunicate message) {
        System.out.println(message.getCommunicate());
    }

    @OnClientError
    @Override
    public void onClientErrorCommunicate(ErrorCommunicate errorCommunicate) {
        System.out.println(errorCommunicate.getCommunicate());
    }

    @OnServerError
    @Override
    public void onServerErrorCommunicate(ErrorCommunicate errorCommunicate) {
        System.out.println(errorCommunicate.getCommunicate());
    }
}
