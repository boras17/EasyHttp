package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.Post;

import java.io.PipedOutputStream;

public class Message extends Post {
    String message;

    public Message(String message) {
        super(message);
    }

    public String getMessage() {
        return this.message;
    }
}
