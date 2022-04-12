package auth;

public class UnauthorizedRequestException extends RuntimeException{
    public UnauthorizedRequestException(String msg){
        super(msg);
    }
}
