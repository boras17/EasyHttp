package auth.digestauth;

import auth.AuthenticationProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DigestAuthenticationProvider extends AuthenticationProvider {

    private DigestConfiguration digestConfiguration;

    public DigestAuthenticationProvider(String username, String password, DigestConfiguration digestConfiguration) {
        super(username, password);
        this.digestConfiguration = digestConfiguration;
    }

    public DigestAuthenticationProvider(String username, String password) {
        super(username, password);
        this.digestConfiguration = new DigestConfiguration.DigestConfigBuilder()
                .build();
    }

    @Override
    public void calculate() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(this.digestConfiguration.getHashAlgorithm().name());

    }
}
