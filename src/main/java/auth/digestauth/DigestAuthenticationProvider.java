package auth.digestauth;

import Headers.Header;
import auth.AuthenticationProvider;
import publishsubscribe.Channels;
import publishsubscribe.Event;
import redirect.ErrorType;
import redirect.GenericError;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DigestAuthenticationProvider extends AuthenticationProvider {

    private DigestResponse digestConfiguration;
    private List<Header> responseHeaders;
    private EasyHttpRequest request;

    public DigestAuthenticationProvider(String username, String password) {
        super(username, password);
    }

    @Override
    public void calculate() throws NoSuchAlgorithmException {
        this.digestConfiguration = DigestResponse.calculateDigestResponse(this.responseHeaders , this.request);

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
                    .concat(this.digestConfiguration.getNonceCount())
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
        digestHeaderVal.append("cnonce=\"").append(this.digestConfiguration.createCnonce()).append("\",");
        digestAuthHeader.setValue(digestHeaderVal.toString());
        this.digestConfiguration.incrementNonceCounter();
        return digestAuthHeader;
    }

    public List<Header> getResponseHeders() {
        return responseHeaders;
    }

    public void setResponse(List<Header> response) {
        this.responseHeaders = response;
    }

    public EasyHttpRequest getRequest() {
        return request;
    }

    public void setRequest(EasyHttpRequest request) {
        this.request = request;
    }


    @Override
    public void on401Response(List<Header> responseHeaders, EasyHttpRequest request) {
        this.digestConfiguration.createCnonce();
        this.digestConfiguration.resetNonceCounter();
        this.setResponse(responseHeaders);
        this.setRequest(request);
        try{
            this.calculate();
        }catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
            Event.operation.publish(Channels.APP_ERROR_CHANNEL,new GenericError(0, Collections.emptyList(),ex.getMessage(), ErrorType.APP));
        }
        List<Header> authHeaders = super.getAuthHeaders();
        request.getHeaders().addAll(authHeaders);
    }

    @Override
    public void beforeRequest(EasyHttpRequest request) {
        this.digestConfiguration.incrementNonceCounter();
        List<Header> authHeader = super.getAuthHeaders();
        request.getHeaders().addAll(authHeader);
    }
}
