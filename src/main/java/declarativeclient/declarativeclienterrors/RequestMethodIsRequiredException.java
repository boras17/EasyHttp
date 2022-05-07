package declarativeclient.declarativeclienterrors;

public class RequestMethodIsRequiredException extends RuntimeException{
    public RequestMethodIsRequiredException(String msg){
        super(msg);
    }
}
