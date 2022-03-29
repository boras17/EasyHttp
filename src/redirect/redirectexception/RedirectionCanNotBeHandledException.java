package redirect.redirectexception;

import redirect.GenericError;

public class RedirectionCanNotBeHandledException extends Exception{
    private GenericError genericError;

    public RedirectionCanNotBeHandledException(GenericError genericError) {
        super(genericError.getMsg());
    }

    public GenericError getGenericError() {
        return this.genericError;
    }
}
