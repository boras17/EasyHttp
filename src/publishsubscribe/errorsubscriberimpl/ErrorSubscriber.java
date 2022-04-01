package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.annotations.OnClientError;
import publishsubscribe.annotations.OnRedirectError;
import publishsubscribe.annotations.OnServerError;
import publishsubscribe.communcates.Communicate;

import java.util.Properties;

public class ErrorSubscriber extends Subscriber{

    private Properties properties;

    public ErrorSubscriber(Properties properties){
        super();
        this.properties = properties;
    }

    @OnRedirectError
    @Override
    public void onRedirectErrorCommunicate(Communicate message) {
    }

    @OnClientError
    @Override
    public void onClientErrorCommunicate(Communicate communicate) {
        System.out.println(communicate.getCommunicate());
    }

    @OnServerError
    @Override
    public void onServerErrorCommunicate(Communicate communicate) {
        System.out.println(communicate.getCommunicate());
    }
}
