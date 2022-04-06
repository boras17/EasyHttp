package publishsubscribe;

public abstract class GenericCommunicate<T> {
    T communicate;

    public GenericCommunicate(T communicate) {
        this.communicate = communicate;
    }

    public GenericCommunicate(){}

    public T getCommunicate() {
        return this.communicate;
    }
}
