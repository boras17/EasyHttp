package client;

import auth.AuthenticationProvider;
import publishsubscribe.Event;
import publishsubscribe.errorsubscriberimpl.Subscriber;
import redirect.RedirectionHandler;
import requests.cookies.CookieExtractor;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

public class EasyHttpBuilder{
    private String userAgent;
    private CookieExtractor cookieExtractor;
    private AuthenticationProvider authenticationProvider;
    private RedirectionHandler redirectionHandler;
    private Duration connectionTimeout;


    public EasyHttpBuilder setConnectionTimeout(Duration connectionTimeout){
        this.connectionTimeout = connectionTimeout;
        return this;
    }
    public EasyHttpBuilder setCookieExtractor(CookieExtractor extractor){
        this.cookieExtractor = extractor;
        return this;
    }
    public EasyHttpBuilder redirectionHandler(RedirectionHandler redirectionHandler){
        this.redirectionHandler = redirectionHandler;
        return this;
    }
    public EasyHttpBuilder setAuthenticationProvider(AuthenticationProvider authenticationProvider){
        this.authenticationProvider = authenticationProvider;
        return this;
    }

    public EasyHttpBuilder setUserAgent(String agent){
        this.userAgent = agent;
        return this;
    }



    public EasyHttp build() throws IOException {
        EasyHttp http = new EasyHttp();
        http.setUserAgent(this.userAgent);
        http.setCookieExtractor(cookieExtractor);
        http.setAuthenticationProvider(this.authenticationProvider);
        http.setRedirectionHandler(this.redirectionHandler);
        http.setConnectionTimeout(this.connectionTimeout);
        http.setConnectionInitializr(new ConnectionInitializr());
        return http;
    }

}