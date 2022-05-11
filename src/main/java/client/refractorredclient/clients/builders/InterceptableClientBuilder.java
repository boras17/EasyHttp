package client.refractorredclient.clients.builders;

import client.ConnectionInitializr;
import client.refractorredclient.AbstractClientBuilder;
import client.refractorredclient.clients.InterceptableClient;
import client.refractorredclient.responsestatushandling.DefaultClientResponseStatusHandler;

public class InterceptableClientBuilder extends AbstractClientBuilder {
    public InterceptableClient build() {
        InterceptableClient interceptableClient = new InterceptableClient(super.getCookieExtractor(),
                super.getAuthenticationProvider(),
                super.getConnectionInitializr() == null ? new ConnectionInitializr() : super.getConnectionInitializr(),
                super.getConnectionTimeout(),
                super.getResponseStatusHandler(), super
                .getAbstractRedirectionHandler());
        interceptableClient.setResponseStatusHandler(new DefaultClientResponseStatusHandler(interceptableClient));
        return interceptableClient;
    }
}
