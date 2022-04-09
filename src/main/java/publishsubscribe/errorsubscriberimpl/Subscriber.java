package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.communcates.ErrorCommunicate;

public class Subscriber<T> {

    public Subscriber() {
    }

    public void onRedirectErrorCommunicate(T redirectError){}

    public void onClientErrorCommunicate(T clientError){}

    public void onServerErrorCommunicate(T serverError){}
}
