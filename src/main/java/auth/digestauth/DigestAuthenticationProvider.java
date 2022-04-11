package auth.digestauth;

import Headers.Header;
import auth.AuthenticationProvider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DigestAuthenticationProvider extends AuthenticationProvider {

    private DigestResponse digestConfiguration;

    public DigestAuthenticationProvider(String username, String password, DigestResponse digestConfiguration) {
        super(username, password);
        this.digestConfiguration = digestConfiguration;
    }

    public DigestAuthenticationProvider(String username, String password) {
        super(username, password);
        this.digestConfiguration = new DigestResponse.DigestConfigBuilder()
                .build();
    }

    @Override
    public void calculate() throws NoSuchAlgorithmException {
        final MessageDigest md =
                MessageDigest.getInstance(this.digestConfiguration
                        .getHashAlgorithm().getCompatibleNameWithMessageDigestAlgorithms());

        final String username = super.getUsername();
        final String password = super.getPassword();
        final String realm = this.digestConfiguration.getRealm();


        String _HA1_hash = null;
        String HA1 = username.concat(":").concat(realm).concat(":").concat(password);

        if(this.digestConfiguration.getHashAlgorithm().name().contains("SESS")){
            byte[] HA1_byte_hash = HA1.getBytes(StandardCharsets.UTF_8);

            _HA1_hash = MD5Util.getMD5Hash(HA1_byte_hash);
            _HA1_hash = MD5Util.getMD5Hash(_HA1_hash.concat(":")
                    .concat(this.digestConfiguration.getNonce()
                    .concat(":").concat(this.digestConfiguration.getCnonce()))
                    .getBytes(StandardCharsets.UTF_8));
        }
        else{
            _HA1_hash = MD5Util.getMD5Hash(HA1.getBytes(StandardCharsets.UTF_8));
        }


        String HA2 = null;
        Set<Qop> qop = this.digestConfiguration.getQop();

        if(qop == null || qop.contains(Qop.AUTH)) {
            md.reset();
            String ha2_auth = this.digestConfiguration
                    .getMethod()
                    .concat(":")
                    .concat(this.digestConfiguration.getUri());
            byte[] ha2_auth_bytes = ha2_auth.getBytes(StandardCharsets.UTF_8);
            md.update(ha2_auth_bytes);
            HA2 =  new String(md.digest());
        }
        if(qop != null && qop.contains(Qop.AUTH_INT)) {
            String entity = this.digestConfiguration.getEntity();
            byte[] entity_bytes = entity.getBytes(StandardCharsets.UTF_8);
            md.update(entity_bytes);
            String entityBodyHash = null;
            try {
                entityBodyHash = MD5Util.getMD5Hash(md.digest());
                HA2 = this.digestConfiguration.getMethod()
                        .concat(":")
                        .concat(this.digestConfiguration.getUri())
                        .concat(":")
                        .concat(entityBodyHash);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        md.reset();
        md.update(HA2.getBytes(StandardCharsets.UTF_8));
        byte[] HA2_hash_bytes = md.digest();

        String HA2_hash = MD5Util.getMD5Hash(HA2_hash_bytes);

        String response = null;

        if(digestConfiguration.isStale()){
            md.reset();
            response = _HA1_hash.concat(":")
                    .concat(this.digestConfiguration.getNonce())
                    .concat(":")
                    .concat(HA2_hash);

        }else{
            response = _HA1_hash.concat(":")
                    .concat(this.digestConfiguration.getNonce())
                    .concat(":")
                    .concat(this.digestConfiguration.getNonceCount())
                    .concat(":")
                    .concat(this.digestConfiguration.getCnonce())
                    .concat(":")
                    .concat(digestConfiguration.getQop().stream()
                            .map(Enum::name)
                            .collect(Collectors.joining()))
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
            String response_hash) {
        String username = super.getUsername();
        Header digestAuthHeader = new Header();

        digestAuthHeader.setKey("Authorization");

        StringBuilder digestHeaderVal = new StringBuilder("Digest username=\"");
        digestHeaderVal.append(username).append("\",");
        digestHeaderVal.append("realm=\"").append(this.digestConfiguration.getRealm()).append("\",");
        digestHeaderVal.append("nonce=\"").append(this.digestConfiguration.getNonce()).append("\",");
        digestHeaderVal.append(this.digestConfiguration.getOpaque() != null ? "opaque=\"".concat(this.digestConfiguration.getOpaque()).concat("\",") : "");
        digestHeaderVal.append("uri=\"").append(this.digestConfiguration.getUri()).append("\",");
        digestHeaderVal.append(this.digestConfiguration.getHashAlgorithm() != null ? "algorithm=\"".concat(this.digestConfiguration.getHashAlgorithm().name()).concat("\","): "");
        digestHeaderVal.append("response=\"").append(response_hash).append("\",");
        digestHeaderVal.append(this.digestConfiguration.getQop().isEmpty() ? "" : "qop=".concat(this.digestConfiguration.getQop().stream()
                .map(Enum::name)
                        .collect(Collectors.joining(",")).concat(",")));
        digestHeaderVal.append("nc=").append(this.digestConfiguration.getNonceCount()).append(",");
        digestHeaderVal.append("cnonce=\"").append(this.digestConfiguration.getCnonce()).append("\",");
        digestAuthHeader.setValue(digestHeaderVal.toString());
        return digestAuthHeader;
    }

     private String encode(final byte[] binaryData) {
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

    private String createCnonce() {
        final SecureRandom rnd = new SecureRandom();
        final byte[] tmp = new byte[8];
        rnd.nextBytes(tmp);
        return encode(tmp);
    }
}
