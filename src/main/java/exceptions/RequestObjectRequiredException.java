package exceptions;

public class RequestObjectRequiredException extends RuntimeException{
    public RequestObjectRequiredException(String msg) {
        super(msg);
    }
}
