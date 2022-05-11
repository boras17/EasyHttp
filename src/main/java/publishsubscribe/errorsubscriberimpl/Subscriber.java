package publishsubscribe.errorsubscriberimpl;

public interface Subscriber<T> {

     default void onRedirectErrorCommunicate(T redirectError){}

     default void onClientErrorCommunicate(T clientError){}

     default void onServerErrorCommunicate(T serverError){}

     default void onAppError(T applicationError){}

     default void onNotification(T notification){}
}
