package HttpEnums;

import java.util.Set;

public enum Method {
    POST, GET, HEAD, PUT, PATCH, OPTIONS;
    public static Set<Method> all() {
        return Set.of(POST, GET, HEAD, PUT, PATCH, OPTIONS);
    }
}
