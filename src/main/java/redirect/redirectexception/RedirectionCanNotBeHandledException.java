package redirect.redirectexception;

import publishsubscribe.communcates.notifications.GenericHttpError;

public class RedirectionCanNotBeHandledException extends Exception{
    private GenericHttpError genericHttpError;

    public RedirectionCanNotBeHandledException(GenericHttpError genericHttpError) {
        super(genericHttpError.getMsg());
    }

    public GenericHttpError getGenericError() {
        return this.genericHttpError;
    }
}
