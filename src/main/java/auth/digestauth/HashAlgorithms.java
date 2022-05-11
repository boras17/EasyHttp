package auth.digestauth;

public enum HashAlgorithms {
    MD5("MD5"),
    MD5_SESS("MD5"),
    SHA_512_256("SHA-256"),
    SHA_512_256_SESS("SHA-256"),
    SHA_256("SHA-512/256"),
    SHA_256_SESS("SHA-512/256");

    String compatibleNameWithMessageDigestAlgorithms;

    HashAlgorithms(String compatibleNameWithMessageDigestAlgorithms) {
        this.compatibleNameWithMessageDigestAlgorithms = compatibleNameWithMessageDigestAlgorithms;
    }

    public String getCompatibleNameWithMessageDigestAlgorithms(){
        return this.compatibleNameWithMessageDigestAlgorithms;
    }

    static HashAlgorithms getByName(String name) {
        return switch (name){
            case "MD5" -> MD5;
            case "MD5-sess" -> MD5_SESS;
            case "SHA-512-256" -> SHA_512_256;
            case "SHA-512-256-sess" -> SHA_512_256_SESS;
            case "SHA-256" -> SHA_256;
            case "SHA-256-sess" -> SHA_256_SESS;
            default -> MD5;
        };
    }
}
