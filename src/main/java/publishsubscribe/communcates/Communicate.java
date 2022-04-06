package publishsubscribe.communcates;

import publishsubscribe.GenericCommunicate;
import redirect.GenericError;

public class Communicate extends GenericCommunicate<GenericError> {
    private GenericError error;

    public Communicate(GenericError genericError){
        super(genericError);
    }
}
