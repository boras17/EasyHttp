package auth.digestauth;

public class DigestConfiguration {
    private final String nonce;
    private final HashAlgorithms hashAlgorithm;
    private final String realm;
    private final String qop;
    private final String nonceCount;
    private final String clientNonce;
    private final String opaque;

    public DigestConfiguration(String nonce,
                               HashAlgorithms hashAlgorithm,
                               String realm,
                               String qop,
                               String nonceCount,
                               String clientNonce,
                               String opaque) {
        this.nonce = nonce;
        this.hashAlgorithm = hashAlgorithm;
        this.realm = realm;
        this.qop = qop;
        this.nonceCount = nonceCount;
        this.clientNonce = clientNonce;
        this.opaque = opaque;
    }

    public static class DigestConfigBuilder{
        private String nonce;
        private HashAlgorithms hashAlgorithm = HashAlgorithms.MD5;
        private String realm;
        private String qop;
        private String nonceCount;
        private String clientNonce;
        private String opaque;

        public DigestConfigBuilder setNonce(String nonce) {
            this.nonce = nonce;
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

        public DigestConfigBuilder setNonceCount(String nonceCount) {
            this.nonceCount = nonceCount;
            return this;
        }

        public DigestConfigBuilder setClientNonce(String clientNonce) {
            this.clientNonce = clientNonce;
            return this;
        }

        public DigestConfigBuilder setOpaque(String opaque) {
            this.opaque = opaque;
            return this;
        }

        public DigestConfiguration build() {
            return new DigestConfiguration(nonce, hashAlgorithm,
                    realm, qop, nonceCount, clientNonce, opaque);
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

    public String getNonceCount() {
        return nonceCount;
    }

    public String getClientNonce() {
        return clientNonce;
    }

    public String getOpaque() {
        return opaque;
    }
}
