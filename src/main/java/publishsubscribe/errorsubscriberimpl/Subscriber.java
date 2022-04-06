package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.communcates.Communicate;

public abstract class Subscriber {

    public Subscriber() {
    }

    public abstract void onRedirectErrorCommunicate(Communicate redirectError);

    public abstract void onClientErrorCommunicate(Communicate clientError);

    public abstract void onServerErrorCommunicate(Communicate serverError);
}
