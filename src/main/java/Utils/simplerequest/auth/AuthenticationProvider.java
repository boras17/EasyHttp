package Utils.simplerequest.auth;

import Headers.HttpHeader;

import java.security.NoSuchAlgorithmException;

public abstract class AuthenticationProvider implements AuthenticateChannel{
    private final String password;
    private final String username;
    private HttpHeader authHttpHeader;

    public AuthenticationProvider(String username, String password){
        this.username = username;
        this.password = password;
    }

    public abstract void calculate() throws NoSuchAlgorithmException;

    public void addAuthHeader(HttpHeader httpHeader) {
        this.authHttpHeader = httpHeader;
    }
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public HttpHeader getAuthHeaders() {
        return authHttpHeader;
    }
}
