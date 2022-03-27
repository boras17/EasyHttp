package intercepting;

@FunctionalInterface
public interface Interceptor<T> {
    void handle(T t);
}
