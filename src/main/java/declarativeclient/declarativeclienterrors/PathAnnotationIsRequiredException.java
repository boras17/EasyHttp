package declarativeclient.declarativeclienterrors;

public class PathAnnotationIsRequiredException extends RuntimeException{
    public PathAnnotationIsRequiredException(String msg){
        super(msg);
    }
}
