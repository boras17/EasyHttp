import HttpEnums.Method;
import auth.digestauth.DigestAuthenticationProvider;
import client.EasyHttp;
import client.EasyHttpBuilder;
import publishsubscribe.Channels;
import publishsubscribe.errorsubscriberimpl.ErrorChannelConfigProp;
import publishsubscribe.errorsubscriberimpl.ErrorSubscriber;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Home {
    public static void main(String[] args) throws IOException, RedirectionUnhandled, IllegalAccessException, NoSuchAlgorithmException, InterruptedException {
        DigestAuthenticationProvider authenticationProvider = new DigestAuthenticationProvider("admin", "admin123");

        Properties properties = new Properties();
        properties.put(ErrorChannelConfigProp.CLIENT_ERROR_FILE, "client-errors.txt");

        EasyHttp easyHttp = new EasyHttpBuilder()
                .setSubscribedChannels(Map.of(Channels.CLIENT_ERROR_CHANNEL, new ErrorSubscriber(properties)))
                .build();

        EasyHttpRequest request = new EasyHttpRequest
                .EasyHttpRequestBuilder()
                .setUri(new URL("http://localhost:4545/hello"))
                .setMethod(Method.GET)
                .build();
        easyHttp.send(request, new EmptyBodyHandler());
    }
}
