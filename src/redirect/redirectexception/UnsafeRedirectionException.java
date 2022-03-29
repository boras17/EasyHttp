package redirect.redirectexception;

import redirect.GenericError;

public class UnsafeRedirectionException extends Exception{
    private GenericError genericError;
    public UnsafeRedirectionException(GenericError genericError) {
        super(genericError.getMsg());
    }

    public GenericError getGenericError() {
        return this.genericError;
    }
}
