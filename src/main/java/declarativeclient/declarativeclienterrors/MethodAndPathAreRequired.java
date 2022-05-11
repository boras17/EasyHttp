package declarativeclient.declarativeclienterrors;

public class MethodAndPathAreRequired extends RuntimeException{
    public MethodAndPathAreRequired(String msg){
        super(msg);
    }
}
