package intercepting;

import java.security.NoSuchAlgorithmException;

@FunctionalInterface
public interface Interceptor<T> {
    void handle(T t);
}
