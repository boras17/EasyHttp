package publishsubscribe.communcates;

import publishsubscribe.GenericCommunicate;
import redirect.GenericError;

public class ErrorCommunicate extends GenericCommunicate<GenericError> {
    public ErrorCommunicate(GenericError genericError){
        super(genericError);
    }
}
