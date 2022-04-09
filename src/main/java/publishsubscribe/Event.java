package publishsubscribe;

public class Event {
    static {
        init();
    }

    public static Operation operation;

    static void init() {
        operation = new Operation();
    }
}