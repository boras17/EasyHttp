package client;

import auth.AuthenticationProvider;
import client.responsestatushandling.ResponseStatusHandler;
import cookies.CookieExtractor;
import redirect.AbstractRedirectionHandler;
import redirect.RedirectionHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.function.Function;

public class GenericClientBuilder<T> {
    T type;
    Class<T> aClass;
    private Function<T,T> decorate;

    public GenericClientBuilder(Class<T> aClass) {
        this.aClass = aClass;
    }

    private CookieExtractor cookieExtractor;
    private AuthenticationProvider authenticationProvider;
    private ConnectionInitializr connectionInitializr;
    private Duration connectionTimeout;
    private ResponseStatusHandler responseStatusHandler;
    private RedirectionHandler abstractRedirectionHandler;

    public void setDecorator(Function<T,T> decorate){
        this.decorate = decorate;
    }

    public GenericClientBuilder<T> cookieExtractor(CookieExtractor cookieExtractor) {
        this.cookieExtractor = cookieExtractor;
        return this;
    }

    public GenericClientBuilder<T> responseStatusHandler(ResponseStatusHandler responseStatusHandler) {
        this.responseStatusHandler = responseStatusHandler;
        return this;
    }

    public GenericClientBuilder<T> authenticationProvider(AuthenticationProvider authenticationProvider){
        this.authenticationProvider = authenticationProvider;
        return this;
    }

    public GenericClientBuilder<T> connectionInitializer(ConnectionInitializr connectionInitializr){
        this.connectionInitializr = connectionInitializr;
        return this;
    }

    public GenericClientBuilder<T> connectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public GenericClientBuilder<T> clientBuilder(RedirectionHandler abstractRedirectionHandler){
        this.abstractRedirectionHandler = abstractRedirectionHandler;
        return this;
    }

    public T build() {
        try{
            Constructor<T> constructor = this.aClass.getConstructor(CookieExtractor.class,
                    AuthenticationProvider.class,
                    ConnectionInitializr.class,
                    Duration.class,
                    AbstractRedirectionHandler.class);

            T client =  constructor.newInstance(this.cookieExtractor,
                    this.authenticationProvider,
                    this.connectionInitializr == null ? new ConnectionInitializr() : this.connectionInitializr,
                    this.connectionTimeout, abstractRedirectionHandler);
            return this.decorate == null ? client : this.decorate.apply(client);
        }catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            throw new RuntimeException(e);
        }
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
