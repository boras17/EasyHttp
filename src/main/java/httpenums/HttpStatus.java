package httpenums;

public enum HttpStatus {
    SUCCESSFUL, REDIRECTED, CLIENT_ERROR, SERVER_ERROR;

    public static HttpStatus getStatus(int responseStatus){
        if(responseStatus>= 200 && responseStatus < 300)
            return SUCCESSFUL;
        else if(responseStatus >= 300 && responseStatus < 400)
            return REDIRECTED;
        else if(responseStatus >= 400 && responseStatus < 500)
            return CLIENT_ERROR;
        else if(responseStatus >= 500)
            return SERVER_ERROR;
        else
            throw new IllegalArgumentException("status: " + responseStatus + " is outside the default response statuses scope");
    }
}
