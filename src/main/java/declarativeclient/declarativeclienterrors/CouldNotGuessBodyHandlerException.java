package declarativeclient.declarativeclienterrors;

public class CouldNotGuessBodyHandlerException extends RuntimeException{
    public CouldNotGuessBodyHandlerException(String msg){
        super(msg);
    }
}
