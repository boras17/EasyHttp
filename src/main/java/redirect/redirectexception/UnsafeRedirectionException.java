package redirect.redirectexception;

import publishsubscribe.communcates.notifications.GenericHttpError;

public class UnsafeRedirectionException extends Exception{
    public UnsafeRedirectionException(String error) {
        super(error);
    }
}
