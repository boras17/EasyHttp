package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.communcates.ErrorCommunicate;

public interface Subscriber<T> {

    default void onRedirectErrorCommunicate(T redirectError){}

    default void onClientErrorCommunicate(T clientError){}

    default void onServerErrorCommunicate(T serverError){}
}
