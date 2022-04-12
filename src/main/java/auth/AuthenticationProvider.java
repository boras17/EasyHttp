package auth;

import Headers.Header;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public abstract class AuthenticationProvider implements AuthenticateChannel{
    private final String password;
    private final String username;
    private final List<Header> authHeaders = new ArrayList<>();

    public AuthenticationProvider(String username, String password){
        this.username = username;
        this.password = password;
    }

    public abstract void calculate() throws NoSuchAlgorithmException;

    public void addAuthHeader(Header header) {
        this.authHeaders.add(header);
    }
    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public List<Header> getAuthHeaders() {
        return authHeaders;
    }
}
