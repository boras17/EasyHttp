package auth.digestauth;

import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DigestResponse {
    private final HashAlgorithms hashAlgorithm;//
    private final String nonce;
    private final String realm; //
    private final String qop;
    private int nonceCount = 1;
    private final String method;
    private final String uri; //
    private final String cnonce;
    private EasyHttpRequest request;
    private final boolean stale;
    private final String opaque;
    private final String entity;

    public DigestResponse(String nonce,
                          HashAlgorithms hashAlgorithm,
                          String realm,
                          String qop,
                          String method,
                          String uri,
                          String cnonce,
                          EasyHttpRequest easyHttpRequest,
                          boolean stale,
                          String opaque,
                          String entity) {
        this.nonce = nonce;
        this.hashAlgorithm = hashAlgorithm;
        this.realm = realm;
        this.qop = qop;
        this.method = method;
        this.uri = uri;
        this.cnonce = cnonce;
        this.request = easyHttpRequest;
        this.stale = stale;
        this.opaque = opaque;
        this.entity = entity;
    }


    public static DigestResponse calculateDigestResponse(EasyHttpResponse<?> response){
        DigestConfigBuilder digestConfigBuilder = new DigestConfigBuilder();

        response.getHeaderByName("WWW-Authenticate")
                .ifPresent(wwwAuthHeader -> {
                    String headerVal = wwwAuthHeader.getValue();
                    String digestVal = headerVal.substring(0,7);
                    String[] digestParts = digestVal.split(",");
                    Map<String, Object> digestPropertyValueMap
                            = Arrays.stream(digestParts)
                            .collect(Collectors.toMap(part -> part.split("=")[0],
                                    (Function<String, String>) part -> part.split("=")[1]));

                    if(digestPropertyValueMap.containsKey("realm")){
                        digestConfigBuilder.setRealm((String)digestPropertyValueMap.get("realm"));
                    }
                    if(digestPropertyValueMap.containsKey("nonce")) {
                        digestConfigBuilder.setNonce((String)digestPropertyValueMap.get("nonce"));
                    }
                    if(digestPropertyValueMap.containsKey("qop")){
                        digestConfigBuilder.setQop((String)digestPropertyValueMap.get("qop"));
                    }
                    if(digestPropertyValueMap.containsKey("opaque")){
                        digestConfigBuilder.setOpaque((String)digestPropertyValueMap.get("opaque"));
                    }
                    if(digestPropertyValueMap.containsKey("stale")) {
                        digestConfigBuilder.setStale((boolean) digestPropertyValueMap.get("stale"));
                    }
                });
        return digestConfigBuilder.build();
    }

    public void incrementNonceCounter() {
        this.nonceCount += 1;
    }

    public String getNonceCount(){
        // 00000001
        String cnonce = String.valueOf(this.nonceCount);
        int size = cnonce.length();
        int maxSize = 8;
        int zeroFill = maxSize - size;
        return "0".repeat(zeroFill).concat(cnonce);
    }

    public static class DigestConfigBuilder{
        private String nonce;
        private HashAlgorithms hashAlgorithm = HashAlgorithms.MD5;
        private String realm;
        private String qop;
        private String method;
        private String uri;
        private String cnonce;
        private EasyHttpRequest easyHttpRequest;
        private boolean stale;
        private String opaque;
        private String entity;

        public DigestConfigBuilder setNonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public DigestConfigBuilder setCnonce(String cnonce) {
            this.cnonce = cnonce;
            return this;
        }
        public DigestConfigBuilder setEntity(String entity) {
            this.entity = entity;
            return this;
        }
        public DigestConfigBuilder setHashAlgorithm(HashAlgorithms hashAlgorithm) {
            this.hashAlgorithm = hashAlgorithm;
            return this;
        }

        public DigestConfigBuilder setRealm(String realm) {
            this.realm = realm;
            return this;
        }

        public DigestConfigBuilder setQop(String qop) {
            this.qop = qop;
            return this;
        }

        public DigestConfigBuilder setMethod(String method) {
            this.method = method;
            return this;
        }

        public DigestConfigBuilder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public DigestConfigBuilder setEntityBody(EasyHttpRequest easyHttpRequest) {
            this.easyHttpRequest = easyHttpRequest;
            return this;
        }

        public DigestConfigBuilder setOpaque(String opaque) {
            this.opaque = opaque;
            return this;
        }

        public DigestConfigBuilder setStale(boolean stale) {
            this.stale = stale;
            return this;
        }

        public DigestResponse build() {
            return new DigestResponse( nonce,
                    hashAlgorithm,
                    realm,
                    qop,
                    method,
                    uri,
                    cnonce,
                    easyHttpRequest,
                    stale,
                    opaque,
                    entity);
        }
    }

    public String getNonce() {
        return nonce;
    }

    public HashAlgorithms getHashAlgorithm() {
        return hashAlgorithm;
    }

    public String getRealm() {
        return realm;
    }

    public String getQop() {
        return qop;
    }

    public String getMethod() {
        return method;
    }

    public EasyHttpRequest getEntityBody() {
        return this.request;
    }

    public String getUri() {
        return uri;
    }

    public void setNonceCount(int nonceCount) {
        this.nonceCount = nonceCount;
    }

    public String getCnonce() {
        return cnonce;
    }

    public EasyHttpRequest getRequest() {
        return request;
    }

    public void setRequest(EasyHttpRequest request) {
        this.request = request;
    }

    public boolean isStale() {
        return stale;
    }

    public String getOpaque() {
        return opaque;
    }

    public String getEntity() {
        return entity;
    }
}
