package declarativeclienttests;

import declarativeclient.declarativeannotations.Header;
import declarativeclient.declarativeannotations.Headers;

public interface DeclaredInterface {
    @Headers(headers = {
            @Header(key = "content-type", value = "application/json"),
            @Header(key = "connection", value = "keep-alive")
    })
    void getPerson();
}
