package redirect.redirectexception;

import publishsubscribe.communcates.notifications.GenericHttpError;

public class UnsafeRedirectionException extends Exception{
    private GenericHttpError genericHttpError;
    public UnsafeRedirectionException(GenericHttpError genericHttpError) {
        super(genericHttpError.getMsg());
    }

    public GenericHttpError getGenericError() {
        return this.genericHttpError;
    }
}
