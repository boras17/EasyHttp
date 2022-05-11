package publishsubscribe.communcates;

import publishsubscribe.ChannelMessageType;

public abstract class GenericCommunicate<T> {
    T communicate;
    ChannelMessageType channelMessageType;
    public GenericCommunicate(T communicate, ChannelMessageType channelMessageType) {
        this.communicate = communicate;
        this.channelMessageType = channelMessageType;
    }

    public GenericCommunicate(){}

    public abstract String formatGenericCommunicate();

    public T getCommunicate() {
        return this.communicate;
    }

    public void setCommunicate(T communicate) {
        this.communicate = communicate;
    }

    public ChannelMessageType getErrorType() {
        return channelMessageType;
    }

    public void setErrorType(ChannelMessageType channelMessageType) {
        this.channelMessageType = channelMessageType;
    }
}
