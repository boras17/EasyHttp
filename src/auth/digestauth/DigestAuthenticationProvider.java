package auth.digestauth;

import Headers.Header;
import auth.AuthenticationProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

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
        String _HA1_hash = MD5Util.getMD5Hash(HA1_byte_hash);

        String HA1_hash = MD5Util.getMD5Hash(_HA1_hash.concat(":")
                .concat(digestConfiguration.getNonce())
                .concat(":")
                .concat(this.createCnonce()).getBytes(StandardCharsets.UTF_8));

        String HA2 = Optional.ofNullable(this.digestConfiguration.getQop())
                .map(qop -> {
                    if(qop.equals("auth-int")){
                        md.reset();
                        md.update(this.digestConfiguration.getEntityBody().getBytes(StandardCharsets.UTF_8));
                        String entityBodyHash = null;
                        try {
                            entityBodyHash = MD5Util.getMD5Hash(md.digest());
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                        return HA2_forQopAuthOrUnspecified();
                    }else{
                        return HA2_forQopAuthOrUnspecified();
                    }
                })
                .orElse(HA2_forQopAuthOrUnspecified());
        md.reset();
        md.update(HA2.getBytes(StandardCharsets.UTF_8));
        byte[] HA2_hash_bytes = md.digest();

        String HA2_hash = MD5Util.getMD5Hash(HA2_hash_bytes);

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
                       // .concat(this.digestConfiguration.getNonceCount())
                       // .concat(":")
                       // .concat(qop)
                       // .concat(":")
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
        String response_hash = MD5Util.getMD5Hash(response_hash_bytes);

        Header digestAuthHeader = this.generateDigestAuthHeader(response_hash);

        super.addAuthHeader(digestAuthHeader);
    }

    private Header generateDigestAuthHeader(
            String response) {
        String username = super.getUsername();
        Header digestAuthHeader = new Header();

        digestAuthHeader.setKey("Authorization");

        StringBuilder headerValue = new StringBuilder("Digest username=\"")
                .append(username)
                .append("\", \n")
                .append("realm=")
                .append("\"")
                .append(this.digestConfiguration.getRealm())
                .append("\", ")
                .append("nonce=\"")
                .append(this.digestConfiguration.getNonce())
                .append("\"")
                .append(", \n")
                .append("uri=")
                .append("\"")
                .append(this.digestConfiguration.getUri())
                .append("\n")
                .append("cnonce=\"").append(createCnonce())
                .append("\", ").append("\n");

        Optional<String> qop = Optional.ofNullable(this.digestConfiguration.getQop());

        if(qop.isPresent()){
            headerValue.append("qop=").append(this.digestConfiguration.getQop()).append(",").append("\n");
        }

        headerValue.append("nc=").append(this.digestConfiguration.getNonceCount()).append(",\n");
        headerValue.append("response=\"").append(response).append("\"").append(",\n");

        digestAuthHeader.setValue("Digest "+
                "username=\"admin\","+
                "realm=\"digest-realm\","+
                "nonce=\"" + this.digestConfiguration.getNonce() + "\","+
                "uri=\"" + this.digestConfiguration.getUri() + "\","+
                "qop=auth,"+
                "nc=00000001,"+ //Increment this each time.
                "cnonce=\"" + createCnonce() + "\","+
                "response=\"" + response + "\"");
        System.out.println(headerValue.toString());
        return digestAuthHeader;
    }


    private String HA2_forQopAuthOrUnspecified(){
        return this.digestConfiguration.getMethod()
                .concat(":")
                .concat(this.digestConfiguration.getUri());
    }

     String encode(final byte[] binaryData) {
        final char[] HEXADECIMAL = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'
        };

        final int n = binaryData.length;
        final char[] buffer = new char[n * 2];
        for (int i = 0; i < n; i++) {
            final int low = (binaryData[i] & 0x0f);
            final int high = ((binaryData[i] & 0xf0) >> 4);
            buffer[i * 2] = HEXADECIMAL[high];
            buffer[(i * 2) + 1] = HEXADECIMAL[low];
        }

        return new String(buffer);
    }

    public  String createCnonce() {
        final SecureRandom rnd = new SecureRandom();
        final byte[] tmp = new byte[8];
        rnd.nextBytes(tmp);
        return encode(tmp);
    }
}
