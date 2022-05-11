package redirect.redirectexception;

import publishsubscribe.communcates.notifications.GenericHttpError;

public class RedirectionUnhandled extends Exception{
    private GenericHttpError genericHttpError;

    public RedirectionUnhandled(GenericHttpError genericHttpError) {
        super(genericHttpError.getMsg());
    }

    public GenericHttpError getGenericError() {
        return this.genericHttpError;
    }
}
