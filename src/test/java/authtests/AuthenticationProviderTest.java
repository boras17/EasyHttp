package authtests;

import Headers.Header;
import HttpEnums.Method;
import auth.AuthenticationProvider;
import auth.BasicAuthenticationProvider;
import auth.digestauth.DigestAuthenticationProvider;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import Utils.simplerequest.EasyHttpRequest;

import java.net.MalformedURLException;
import java.net.URL;
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

    @Test
    public void digestTest() throws MalformedURLException {
        DigestAuthenticationProvider digestAuthenticationProvider = new DigestAuthenticationProvider("admin","admin123");

        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setMethod(Method.GET)
                .setUri(new URL("http://localhost:4545/hello"))
                .build();
        String expectedResponse = "6629fae49393a05397450978507c4ef1";

        Header responseDigestHeader = new Header();
        responseDigestHeader.setKey("WWW-Authenticate");
        responseDigestHeader.setValue("Digest realm=\"digest-realm\", qop=\"auth\", nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\"");

        digestAuthenticationProvider.on401Response(List.of(responseDigestHeader), request);

        request.getHeaders()
                .stream()
                .filter(header->header.getKey().equals("Authorization"))
                .findFirst()
                .ifPresent(header -> {
                    System.out.println(header.getValue());
                });
        String data = "d3b9e975723c8666a9cec9bdb6f1bdd7";
    }

    @Test
    public void testDigest() throws MalformedURLException {

    }
}
