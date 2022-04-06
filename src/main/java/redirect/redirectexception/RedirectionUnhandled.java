package redirect.redirectexception;

import redirect.GenericError;

public class RedirectionUnhandled extends Exception{
    private GenericError genericError;

    public RedirectionUnhandled(GenericError genericError) {
        super(genericError.getMsg());
    }

    public GenericError getGenericError() {
        return this.genericError;
    }
}
