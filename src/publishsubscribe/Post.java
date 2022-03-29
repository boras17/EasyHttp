package publishsubscribe;

public abstract class Post {
    String message;

    public Post(String message) {
        this.message = message;
    }

    public Post(){}
}
