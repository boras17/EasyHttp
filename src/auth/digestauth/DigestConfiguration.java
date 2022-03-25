package auth.digestauth;

public class DigestConfiguration {
    private final HashAlgorithms hashAlgorithm;
    // digest resp part
    private final String nonce; //
    private final String realm; //
    private final String qop; //
    private int nonceCount = 1;
    //-----
    private final String method;
    private final String entityBody;
    private final String uri;
    //

    private final String cnonce;

    public DigestConfiguration(String nonce,
                               HashAlgorithms hashAlgorithm,
                               String realm,
                               String qop,
                               String method,
                               String uri,
                               String cnonce,
                               String entityBody) {
        this.nonce = nonce;
        this.hashAlgorithm = hashAlgorithm;
        this.realm = realm;
        this.qop = qop;
        this.method = method;
        this.uri = uri;
        this.cnonce = cnonce;
        this.entityBody = entityBody;
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
        private String entityBody;

        public DigestConfigBuilder setNonce(String nonce) {
            this.nonce = nonce;
            return this;
        }
        public DigestConfigBuilder setCnonce(String cnonce) {
            this.cnonce = cnonce;
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

        public DigestConfigBuilder setEntityBody(String entityBody) {
            this.entityBody = entityBody;
            return this;
        }


        public DigestConfiguration build() {
            return new DigestConfiguration( nonce,
                                            hashAlgorithm,
                                            realm,
                                            qop,
                                            method,
                                            uri,
                                            cnonce,
                                            entityBody);
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

    public String getEntityBody() {
        return entityBody;
    }

    public String getUri() {
        return uri;
    }

}
