package auth.digestauth;

import requests.easyresponse.EasyHttpResponse;

public class DigestConfigurationInitializer {
    private DigestResponse digestConfiguration;


    public DigestResponse getInitializedDigestConfigurationFromResponse(EasyHttpResponse<?> response) {

        return this.digestConfiguration;
    }
}
