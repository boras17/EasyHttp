package client.refractorredclient.clients.builders;

import client.ConnectionInitializr;
import client.refractorredclient.AbstractClientBuilder;
import client.refractorredclient.EasyHttpClient;
import client.refractorredclient.clients.DefaultClient;
import client.refractorredclient.responsestatushandling.DefaultClientResponseStatusHandler;

public class DefaultClientBuilder extends AbstractClientBuilder {
    public DefaultClient build(){
        EasyHttpClient client = super.build();
        client.setConnectionInitializr(new ConnectionInitializr());
        client.setResponseStatusHandler(new DefaultClientResponseStatusHandler(client));
        return (DefaultClient)client;
    }
}
