package auth.digestauth;

import jdk.jfr.Frequency;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DigestResponse {
    private final HashAlgorithms hashAlgorithm;//
    private final String nonce;
    private final String realm; //
    private final Set<Qop> qop;
    private int nonceCount = 1;
    private final String method;
    private final String uri; //
    private final String cnonce;
    private EasyHttpRequest request;
    private final boolean stale;
    private final String opaque;
    private final String entity;
    private final String authParam;

    public DigestResponse(String nonce,
                          HashAlgorithms hashAlgorithm,
                          String realm,
                          String method,
                          String uri,
                          String cnonce,
                          EasyHttpRequest easyHttpRequest,
                          boolean stale,
                          String opaque,
                          String entity,
                          Set<Qop> qop,
                          String authParam) {
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
        this.authParam = authParam;
    }


    public static DigestResponse calculateDigestResponse(EasyHttpResponse<?> response, EasyHttpRequest request){
        DigestConfigBuilder digestConfigBuilder = new DigestConfigBuilder();

        response.getHeaderByName("WWW-Authenticate")
                .ifPresent(wwwAuthHeader -> {
                    String headerVal = wwwAuthHeader.getValue();
                    String digestVal = headerVal.substring(7);

                    String[] digestParts = digestVal.split(", ");
                    Map<String, Object> digestPropertyValueMap
                            = Arrays.stream(digestParts)
                            .collect(Collectors.toMap(part -> part.split("=")[0],
                                    (Function<String, String>) part -> {
                                        String[] parts = part.split("=");
                                        int len = parts.length;
                                        if(len == 1){
                                            return "";
                                        }else{
                                            return parts[1];
                                        }
                                    }));
                    for(Map.Entry<String, Object> entreis: digestPropertyValueMap.entrySet()){
                        System.out.println(entreis.getKey());
                        System.out.println(entreis.getValue());
                    }

                    if(digestPropertyValueMap.containsKey("realm")){
                        digestConfigBuilder.setRealm((String)digestPropertyValueMap.get("realm"));
                    }
                    if(digestPropertyValueMap.containsKey("nonce")) {
                        digestConfigBuilder.setNonce((String)digestPropertyValueMap.get("nonce"));
                    }
                    if(digestPropertyValueMap.containsKey("auth-param")) {
                        digestConfigBuilder.setAuthParam((String)digestPropertyValueMap.get("auth-param"));
                    }
                    if(digestPropertyValueMap.containsKey("qop")){
                        String qopPart = (String)digestPropertyValueMap.get("qop");
                        for(String qop: qopPart.split(",")){
                            Qop enum_qop = switch (qop){
                                case "auth-int" -> Qop.AUTH_INT;
                                default -> Qop.AUTH;
                            };
                            digestConfigBuilder.addQop(enum_qop);
                        }
                    }
                    if(digestPropertyValueMap.containsKey("opaque")){
                        digestConfigBuilder.setOpaque((String)digestPropertyValueMap.get("opaque"));
                    }
                    if(digestPropertyValueMap.containsKey("stale")) {
                        digestConfigBuilder.setStale((boolean) digestPropertyValueMap.get("stale"));
                    }
                    if(digestPropertyValueMap.containsKey("algorithm")) {
                        digestConfigBuilder
                                .setHashAlgorithm(
                                        HashAlgorithms.getByName((String)digestPropertyValueMap.get("algorithm"))
                                );
                    }
                });
        digestConfigBuilder.setMethod(request.getMethod().name());
        digestConfigBuilder.setUri(request.getUrl().getPath());
        return digestConfigBuilder.build();
    }

    public void incrementNonceCounter() {
        this.nonceCount += 1;
    }

    public String getNonceCount(){
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
        private Set<Qop> qop = new HashSet<>();
        private String method;
        private String uri;
        private String cnonce;
        private EasyHttpRequest easyHttpRequest;
        private boolean stale;
        private String opaque;
        private String entity;
        private String authParam;

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

        public DigestConfigBuilder setAuthParam(String authParam){
            this.authParam = authParam;
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

        public DigestConfigBuilder setMethod(String method) {
            this.method = method;
            return this;
        }

        public DigestConfigBuilder setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public DigestConfigBuilder addQop(Qop qop) {
            this.qop.add(qop);
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
                    method,
                    uri,
                    cnonce,
                    easyHttpRequest,
                    stale,
                    opaque,
                    entity,
                    qop,
                    authParam);
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

    public Set<Qop> getQop() {
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

    public String getAuthParam() {
        return authParam;
    }

    public String getEntity() {
        return entity;
    }
}
