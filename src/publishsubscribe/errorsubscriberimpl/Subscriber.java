package publishsubscribe.errorsubscriberimpl;

import publishsubscribe.OnMessage;

public abstract class Subscriber {

    public Subscriber() {
    }

    @OnMessage
    public abstract void onMessage(Message message);
}
