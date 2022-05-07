package Utils.simplerequest.auth.digestauth;

import Headers.HttpHeader;
import Utils.simplerequest.auth.AuthenticationProvider;
import Utils.simplerequest.EasyHttpRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class DigestAuthenticationProvider extends AuthenticationProvider {

    private DigestResponse digestConfiguration;
    private List<HttpHeader> responseHttpHeaders;
    private EasyHttpRequest request;
    private String response;

    public DigestAuthenticationProvider(String username, String password) {
        super(username, password);
    }
    public DigestAuthenticationProvider(String username, String password, DigestResponse response){
        super(username, password);
        this.digestConfiguration = response;
    }
    @Override
    public void calculate() throws NoSuchAlgorithmException {
        this.digestConfiguration = this.digestConfiguration == null
                ?
                DigestResponse.calculateDigestResponse(this.responseHttpHeaders, this.request)
                :
                this.digestConfiguration;

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
            _HA1_hash = MD5Util.getMD5Hash(HA1.getBytes(StandardCharsets.US_ASCII));
        }

        String HA2 = null;
        Set<Qop> qop = this.digestConfiguration.getQop();

        if(qop == null || qop.contains(Qop.AUTH)) {
            md.reset();
            String ha2_auth = this.digestConfiguration
                    .getMethod()
                    .concat(":")
                    .concat(this.digestConfiguration.getUri());
            byte[] ha2_auth_bytes = ha2_auth.getBytes(StandardCharsets.US_ASCII);
            md.update(ha2_auth_bytes);
            HA2 =MD5Util.getMD5Hash(md.digest());
        }
        if(qop != null && qop.contains(Qop.AUTH_INT)) {
            String entity = this.digestConfiguration.getEntity();
            byte[] entity_bytes = entity.getBytes(StandardCharsets.US_ASCII);
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
        md.update(HA2.getBytes(StandardCharsets.US_ASCII));
        byte[] HA2_hash_bytes = md.digest();

        String HA2_hash = MD5Util.getMD5Hash(HA2_hash_bytes);

        String response = null;

        if(qop == null){
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
                    .concat(this.digestConfiguration.createCnonce())
                    .concat(":")
                    .concat(digestConfiguration.getQop().stream()
                            .map(Enum::name)
                            .collect(Collectors.joining()).toLowerCase(Locale.ROOT))
                    .concat(":").concat(HA2_hash);
        }
        md.reset();
        md.update(response.getBytes(StandardCharsets.US_ASCII));
        byte[] response_hash_bytes = md.digest();
        String response_hash = MD5Util.getMD5Hash(response_hash_bytes);
        this.response = response_hash;
        HttpHeader digestAuthHttpHeader = this.generateDigestAuthHeader(response_hash);
        super.addAuthHeader(digestAuthHttpHeader);
    }

    private HttpHeader generateDigestAuthHeader(
            String response_hash) {
        String username = super.getUsername();
        HttpHeader digestAuthHttpHeader = new HttpHeader();

        digestAuthHttpHeader.setKey("Authorization");

        StringBuilder digestHeaderVal = new StringBuilder("Digest username=\"");
        digestHeaderVal.append(username).append("\",");
        digestHeaderVal.append("realm=\"").append(this.digestConfiguration.getRealm()).append("\", ");
        digestHeaderVal.append("nonce=\"").append(this.digestConfiguration.getNonce()).append("\", ");
        digestHeaderVal.append(this.digestConfiguration.getOpaque() != null ? "opaque=\"".concat(this.digestConfiguration.getOpaque()).concat("\", ") : "");
        digestHeaderVal.append("uri=\"").append(this.digestConfiguration.getUri()).append("\", ");
        digestHeaderVal.append(this.digestConfiguration.getHashAlgorithm() != null ? "algorithm=".concat(this.digestConfiguration.getHashAlgorithm().name()).concat(", "): "");
        digestHeaderVal.append("response=\"").append(response_hash).append("\", ");
        digestHeaderVal.append(this.digestConfiguration.getQop().isEmpty() ? "" : "qop=".concat(this.digestConfiguration.getQop().stream()
                .map(Enum::name)
                        .collect(Collectors.joining(",")).toLowerCase(Locale.ROOT).concat(", ")));
        digestHeaderVal.append("nc=").append(this.digestConfiguration.getNonceCount()).append(", ");
        digestHeaderVal.append("cnonce=\"").append(this.digestConfiguration.createCnonce()).append("\"");
        digestAuthHttpHeader.setValue(digestHeaderVal.toString());
        return digestAuthHttpHeader;
    }
    @Override
    public void on401Response(List<HttpHeader> responseHttpHeaders, EasyHttpRequest request) {
        this.digestConfiguration = DigestResponse.calculateDigestResponse(responseHttpHeaders, request);
        this.digestConfiguration.createCnonce();
        this.digestConfiguration.resetNonceCounter();
        this.setResponse(responseHttpHeaders);
        this.setRequest(request);
        try{
            this.calculate();
        }catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
            //Event.operation.publish(Channels.APP_ERROR_CHANNEL,new GenericError(0, Collections.emptyList(),ex.getMessage(), ErrorType.APP));
        }
        HttpHeader authHttpHeader = super.getAuthHeaders();
        request.getHeaders().removeIf(header -> header.getKey().equals(authHttpHeader.getKey()));
        request.getHeaders().add(authHttpHeader);
    }

    @Override
    public void beforeRequest(EasyHttpRequest request) {
        if(this.digestConfiguration == null) return;
        try{
            this.digestConfiguration.incrementNonceCounter();
            this.calculate();
        }catch (NoSuchAlgorithmException noSuchAlgorithmException){
            noSuchAlgorithmException.printStackTrace();
        }
        HttpHeader authHttpHeader = super.getAuthHeaders();
        request.getHeaders().add(authHttpHeader);
    }

    public DigestResponse getDigestConfiguration() {
        return digestConfiguration;
    }

    public void setDigestConfiguration(DigestResponse digestConfiguration) {
        this.digestConfiguration = digestConfiguration;
    }

    public List<HttpHeader> getResponseHeders() {
        return responseHttpHeaders;
    }

    public void setResponse(List<HttpHeader> response) {
        this.responseHttpHeaders = response;
    }

    public EasyHttpRequest getRequest() {
        return request;
    }

    public void setRequest(EasyHttpRequest request) {
        this.request = request;
    }


    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
