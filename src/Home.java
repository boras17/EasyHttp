import HttpEnums.Method;
import publishsubscribe.Channels;
import publishsubscribe.errorsubscriberimpl.ErrorSubscriber;
import publishsubscribe.errorsubscriberimpl.Subscriber;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class Home {
    public static void main(String[] args) throws IOException, IllegalAccessException, RedirectionUnhandled {

        Properties properties = new Properties();
        properties.put("redirectErrors", Paths.get("redirect errors"));

        Subscriber subscriber = new ErrorSubscriber(properties);

        Map<String, Subscriber> map = Collections.singletonMap(Channels.ERROR_CHANNEL,
                subscriber);

        EasyHttp easyHttp = new EasyHttp.EasyHttpBuilder()
                .subscribeForChannels(map)
                .build();
        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setUri(new URL("http://localhost:3232/cookie"))
                .setMethod(Method.POST)
                .build();
        //EasyHttpResponse<Void> response = easyHttp.send(request, new EmptyBodyHandler());

    }
}
