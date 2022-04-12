package publishsubscribe;

import publishsubscribe.annotations.OnAppError;
import publishsubscribe.annotations.OnClientError;
import publishsubscribe.annotations.OnRedirectError;
import publishsubscribe.annotations.OnServerError;
import publishsubscribe.communcates.ErrorCommunicate;
import redirect.ErrorType;
import redirect.GenericError;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Operation extends Event {

    ConcurrentHashMap<String, WeakReference<Object>> channels;

    public Operation() {
        this(new ConcurrentHashMap<>());
    }

    public Operation(ConcurrentHashMap<String, WeakReference<Object>> channels) {
        this.channels = channels;
    }

    public void subscribe(String channelName, Object subscriber) {
        this.channels.put(channelName, new WeakReference<>(subscriber));
    }

    private void publishErrors(Object subscriberObj, GenericCommunicate<?> message){
        GenericError genericError =  ((GenericError)message.getCommunicate());
        ErrorType errorType = genericError.getErrorType();
        final Method[] methods = subscriberObj.getClass().getDeclaredMethods();

        Method method = Stream.of(methods)
                .filter(_method -> {
                    return switch (errorType) {
                        case REDIRECT -> _method.isAnnotationPresent(OnRedirectError.class);
                        case CLIENT -> _method.isAnnotationPresent(OnClientError.class);
                        case SERVER -> _method.isAnnotationPresent(OnServerError.class);
                        case APP -> _method.isAnnotationPresent(OnAppError.class);
                    };
                }).findFirst().orElseThrow();

        Annotation annotation = null;

        switch (errorType){
            case REDIRECT -> {
                annotation = method.getAnnotation(OnRedirectError.class);
            }
            case CLIENT -> {
                annotation = method.getAnnotation(OnClientError.class);
            }
            case SERVER -> {
                annotation = method.getAnnotation(OnServerError.class);
            }
            case APP -> {
                annotation = method.getAnnotation(OnAppError.class);
            }
        }
        if (annotation != null) {

            deliverMessage(subscriberObj, method, message);
        }
    }

    public void publish(String channelName, GenericCommunicate<?> message) {

        WeakReference<?> subscriberRef = this.channels.get(channelName);
        Object subscriberObj = subscriberRef.get();

        if(message != null && subscriberObj != null) {
            System.out.println("DATA");
            this.publishErrors(subscriberObj, message);
        }else{
            System.err.println("An attempt to publish error occured but you did not register any related Subscriber");
        }
    }

    private  <T> void deliverMessage(T subscriber, Method method, GenericCommunicate<?> message) {
        try {
            boolean methodFound = false;
            for (final Class<?> paramClass : method.getParameterTypes()) {
                if (paramClass.equals(message.getClass())) {
                    methodFound = true;
                    break;
                }
            }
            if (methodFound) {
                method.setAccessible(true);
                method.invoke(subscriber, message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}