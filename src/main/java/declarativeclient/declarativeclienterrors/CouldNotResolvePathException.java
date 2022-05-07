package declarativeclient.declarativeclienterrors;

public class CouldNotResolvePathException extends RuntimeException{
    public CouldNotResolvePathException(String msg) {
        super(msg);
    }
}
