package authtests;

import Headers.Header;
import auth.AuthenticationProvider;
import auth.BasicAuthenticationProvider;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public class AuthenticationProviderTest {

    @Test
    public void givenUsernameAndPasswordShouldReturnBasicAuthHeader() throws NoSuchAlgorithmException {
        String username = "login";
        String password = "password";

        AuthenticationProvider authenticationProvider = new BasicAuthenticationProvider(username, password);
        authenticationProvider.calculate();

        Header authHeaders = authenticationProvider.getAuthHeaders();

        Base64.Encoder encoder = Base64.getEncoder();

        String usernamePassword = username.concat(":").concat(password);
        String base64Encoded = encoder.encodeToString(usernamePassword.getBytes(StandardCharsets.UTF_8));
        String expectedHeaderValue = "Basic ".concat(base64Encoded);

        Assertions.assertNotNull(authHeaders);
        String headerName = authHeaders.getKey();
        String headerValue = authHeaders.getValue();
        Assertions.assertEquals("Authorization", headerName);
        Assertions.assertEquals(headerValue, expectedHeaderValue);
    }
}
