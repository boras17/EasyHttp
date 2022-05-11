package client.refractorredclient.clients.builders;

import client.ConnectionInitializr;
import client.refractorredclient.clients.LoggableClient;
import client.refractorredclient.clients.loggingmodel.ClientSubscribers;
import redirect.LoggableRedirectionHandlerDecoreator;

public class LoggableClientBuilder extends DefaultClientBuilder {
    private ClientSubscribers clientSubscribers;

    public LoggableClientBuilder subscribers(ClientSubscribers clientSubscribers){
        this.clientSubscribers = clientSubscribers;
        return this;
    }
    @Override
    public LoggableClient build() {
        return new LoggableClient(super.getCookieExtractor(),
                super.getAuthenticationProvider(),
                new ConnectionInitializr(),
                super.getConnectionTimeout(), this.clientSubscribers,new LoggableRedirectionHandlerDecoreator(super.getAbstractRedirectionHandler()));
    }
}
