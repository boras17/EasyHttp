package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.annotations.OnClientError;
import publishsubscribe.annotations.OnRedirectError;
import publishsubscribe.annotations.OnServerError;
import publishsubscribe.communcates.ErrorCommunicate;

public abstract class Subscriber {

    public Subscriber() {
    }

    @OnRedirectError
    public abstract void onRedirectErrorCommunicate(ErrorCommunicate redirectError);

    @OnClientError
    public abstract void onClientErrorCommunicate(ErrorCommunicate clientError);

    @OnServerError
    public abstract void onServerErrorCommunicate(ErrorCommunicate serverError);
}
