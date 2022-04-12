package auth;

import Headers.Header;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class BasicAuthenticationProvider extends AuthenticationProvider{

    public BasicAuthenticationProvider(String username, String password) {
        super(username, password);
    }

    @Override
    public void calculate() {
        Base64.Encoder base64Encoder = Base64.getEncoder();
        String usernamePassword = super.getUsername().concat(":").concat(super.getPassword());
        byte[] usernamePasswordBytes = usernamePassword.getBytes(StandardCharsets.UTF_8);
        String base64EncodedCredentials = base64Encoder.encodeToString(usernamePasswordBytes);
        String headerValue = "Basic ".concat(base64EncodedCredentials);
        Header authHeader = new Header("Authorization", headerValue);
        super.addAuthHeader(authHeader);
    }

    @Override
    public void beforeRequest(EasyHttpRequest request) {
        Header headers = super.getAuthHeaders();
        request.getHeaders().add(headers);
    }
}
