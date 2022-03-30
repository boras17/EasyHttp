package publishsubscribe;

import publishsubscribe.annotations.OnRedirectError;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class Operation extends Event {

    public void subscribe(String channelName, Object subscriber) {
        channels.put(channelName, new WeakReference<>(subscriber));
    }

    public void publish(String channelName, GenericCommunicate<?> message) {
        WeakReference<?> subscriberRef = channels.get(channelName);
        Object subscriberObj = subscriberRef.get();

        for (final Method method : subscriberObj.getClass().getDeclaredMethods()) {
            Annotation annotation = method.getAnnotation(OnRedirectError.class);
            if (annotation != null) {
                deliverMessage(subscriberObj, method, message);
            }
        }
    }

    private  <T, P extends GenericCommunicate<?>> boolean deliverMessage(T subscriber, Method method, GenericCommunicate<?> message) {
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

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}