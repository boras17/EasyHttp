package client.clients.builders;

import client.ConnectionInitializr;
import client.AbstractClientBuilder;
import client.EasyHttpClient;
import client.clients.DefaultClient;
import client.responsestatushandling.DefaultClientResponseStatusHandler;

public class DefaultClientBuilder extends AbstractClientBuilder {
    public DefaultClient build(){
        EasyHttpClient client = super.build();
        client.setConnectionInitializr(new ConnectionInitializr());
        client.setResponseStatusHandler(new DefaultClientResponseStatusHandler(client));
        return (DefaultClient)client;
    }
}
