package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.annotations.OnClientError;
import publishsubscribe.annotations.OnRedirectError;
import publishsubscribe.annotations.OnServerError;
import publishsubscribe.communcates.ErrorCommunicate;

public abstract class Subscriber {

    public Subscriber() {
    }

    public abstract void onRedirectErrorCommunicate(ErrorCommunicate redirectError);

    public abstract void onClientErrorCommunicate(ErrorCommunicate clientError);

    public abstract void onServerErrorCommunicate(ErrorCommunicate serverError);
}
