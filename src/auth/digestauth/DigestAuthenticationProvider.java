package auth.digestauth;

import Headers.Header;
import auth.AuthenticationProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

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
        final MessageDigest md =
                MessageDigest.getInstance(this.digestConfiguration.getHashAlgorithm().name());

        final String username = super.getUsername();
        final String password = super.getPassword();
        final String realm = this.digestConfiguration.getRealm();

        String HA1 = username.concat(":").concat(realm).concat(":").concat(password);
        byte[] HA1_byte_hash = HA1.getBytes(StandardCharsets.UTF_8);
        String HA1_hash = new String(HA1_byte_hash);

        String HA2 = Optional.ofNullable(this.digestConfiguration.getQop())
                .map(qop -> {
                    if(qop.equals("auth-int")){
                        md.reset();
                        md.update(this.digestConfiguration.getEntityBody().getBytes(StandardCharsets.UTF_8));
                        String entityBodyHash = new String(md.digest());
                        return this.digestConfiguration.getMethod()
                                .concat(":")
                                .concat(this.digestConfiguration.getUri())
                                .concat(":")
                                .concat(entityBodyHash);
                    }else{
                        return HA2_forQopAuthOrUnspecified();
                    }
                })
                .orElse(HA2_forQopAuthOrUnspecified());
        md.reset();
        md.update(HA2.getBytes(StandardCharsets.UTF_8));
        byte[] HA2_hash_bytes = md.digest();

        String HA2_hash = new String(HA2_hash_bytes);

        Optional<String> qopOptional =
                Optional.ofNullable(this.digestConfiguration.getQop());

        String response = null;

        if(qopOptional.isPresent()){
            String qop = qopOptional.get();
            if(qop.equals("auth-int") || qop.equals("auth")){
                md.reset();
                response = HA1_hash.concat(":")
                        .concat(this.digestConfiguration.getNonce())
                        .concat(":")
                        .concat(this.digestConfiguration.getNonceCount())
                        .concat(":")
                        .concat(qop)
                        .concat(":")
                        .concat(HA2_hash);
            }
        }else{
            response = HA1_hash.concat(":")
                    .concat(this.digestConfiguration.getNonce())
                    .concat(":").concat(HA2_hash);
        }
        md.reset();
        md.update(response.getBytes(StandardCharsets.UTF_8));
        byte[] response_hash_bytes = md.digest();
        String response_hash = new String(response_hash_bytes);

        Header digestAuthHeader = this.generateDigestAuthHeader(response_hash);
        System.out.println(digestAuthHeader.getKey());
        System.out.println(digestAuthHeader.getValue());
        super.addAuthHeader(digestAuthHeader);
    }

    private Header generateDigestAuthHeader(
            String response) {
        String username = super.getUsername();
        Header digestAuthHeader = new Header();

        digestAuthHeader.setValue("Authorization");

        StringBuilder headerValue = new StringBuilder("Digest username=\"")
                .append(username)
                .append("\", \n")
                .append("realm=")
                .append("\"\n")
                .append(this.digestConfiguration.getRealm())
                .append("\",")
                .append("nonce=\"")
                .append(this.digestConfiguration.getNonce())
                .append("\"")
                .append(",\n")
                .append("uri=")
                .append("\"")
                .append(this.digestConfiguration.getUri())
                .append("\",").append("\n");

        Optional<String> qop = Optional.ofNullable(this.digestConfiguration.getQop());

        if(qop.isPresent()){
            headerValue.append("qop=").append(this.digestConfiguration.getQop()).append(",").append("\n");
        }

        headerValue.append("nc=").append(this.digestConfiguration.getNonceCount()).append(",\n");
        headerValue.append("response=\"").append(response).append("\"").append(",\n");

        digestAuthHeader.setValue(headerValue.toString());

        return digestAuthHeader;
    }


    private String HA2_forQopAuthOrUnspecified(){
        return this.digestConfiguration.getMethod()
                .concat(":")
                .concat(this.digestConfiguration.getUri());
    }

    private String generateClientNonce() {
        char[] data = {'a','d','f','v','z','h','u','e'};
        int data_len = data.length;
        Random random = new Random();
        StringBuilder client_nonce = new StringBuilder();
        for(int i = 0; i < data_len; ++i){
            client_nonce.append(data[random.nextInt(0, data_len-1)]);
        }
        return client_nonce.toString();
    }
}
