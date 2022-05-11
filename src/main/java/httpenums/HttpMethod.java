package httpenums;

import java.util.Set;

public enum HttpMethod {
    POST, GET, HEAD, PUT, PATCH, OPTIONS, DELETE;
    public static Set<HttpMethod> all() {
        return Set.of(POST, GET, HEAD, PUT, PATCH, OPTIONS, DELETE);
    }
}
