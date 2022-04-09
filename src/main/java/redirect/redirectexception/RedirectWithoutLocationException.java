package redirect.redirectexception;

public class RedirectWithoutLocationException extends RuntimeException {
    public RedirectWithoutLocationException(String msg){
        super(msg);
    }
}
