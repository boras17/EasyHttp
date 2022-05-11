package client.refractorredclient;

import auth.AuthenticationProvider;
import client.ConnectionInitializr;
import client.refractorredclient.clients.DefaultClient;
import client.refractorredclient.responsestatushandling.ResponseStatusHandler;
import cookies.CookieExtractor;
import redirect.AbstractRedirectionHandler;
import redirect.RedirectionHandler;

import java.time.Duration;

public class AbstractClientBuilder {
    private CookieExtractor cookieExtractor;
    private AuthenticationProvider authenticationProvider;
    private ConnectionInitializr connectionInitializr;
    private Duration connectionTimeout;
    private ResponseStatusHandler responseStatusHandler;
    private RedirectionHandler abstractRedirectionHandler;

    public AbstractClientBuilder cookieExtractor(CookieExtractor cookieExtractor) {
        this.cookieExtractor = cookieExtractor;
        return this;
    }

    protected AbstractClientBuilder responseStatusHandler(ResponseStatusHandler responseStatusHandler) {
        this.responseStatusHandler = responseStatusHandler;
        return this;
    }

    public AbstractClientBuilder authenticationProvider(AuthenticationProvider authenticationProvider){
        this.authenticationProvider = authenticationProvider;
        return this;
    }

    public AbstractClientBuilder connectionInitializer(ConnectionInitializr connectionInitializr){
        this.connectionInitializr = connectionInitializr;
        return this;
    }

    public AbstractClientBuilder connectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public AbstractClientBuilder clientBuilder(RedirectionHandler abstractRedirectionHandler){
        this.abstractRedirectionHandler = abstractRedirectionHandler;
        return this;
    }

    protected EasyHttpClient build() {
        return new DefaultClient(this.cookieExtractor,
                this.authenticationProvider,
                this.connectionInitializr == null ? new ConnectionInitializr() : this.connectionInitializr,
                this.connectionTimeout,
                this.responseStatusHandler, abstractRedirectionHandler);
    }

    public CookieExtractor getCookieExtractor() {
        return cookieExtractor;
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public ConnectionInitializr getConnectionInitializr() {
        return connectionInitializr;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public ResponseStatusHandler getResponseStatusHandler() {
        return responseStatusHandler;
    }

    public RedirectionHandler getAbstractRedirectionHandler() {
        return abstractRedirectionHandler;
    }
}
