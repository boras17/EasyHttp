package jsonoperations.serialization;

public abstract class EasySerializer<T> {
    public abstract String serialize(T object);
}
