package exceptions;

public class ResponseHandlerRequired extends RuntimeException{
    public ResponseHandlerRequired(String msg){
        super(msg);
    }
}
