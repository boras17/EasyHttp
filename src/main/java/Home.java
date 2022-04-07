import HttpEnums.Method;
import client.EasyHttp;
import client.EasyHttpBuilder;
import intercepting.EasyResponseInterceptor;
import intercepting.Interceptor;
import jsonoperations.JsonCreator;
import publishsubscribe.Channels;
import publishsubscribe.errorsubscriberimpl.ErrorSubscriber;
import publishsubscribe.errorsubscriberimpl.Subscriber;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.cookies.CookieExtractor;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class Home {

    public static void main(String[] args) throws IOException, IllegalAccessException, RedirectionUnhandled {

        Properties properties = new Properties();
        properties.put("redirectErrors", Paths.get("redirect errors"));

        Subscriber subscriber = new ErrorSubscriber(properties);

        Map<String, Subscriber> map = Collections.singletonMap(Channels.ERROR_CHANNEL,
                subscriber);
        CookieExtractor cookieExtractor = new CookieExtractor();




    }
}
