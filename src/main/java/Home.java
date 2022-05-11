import client.ConnectionInitializr;
import client.refractorredclient.clients.LoggableClient;
import client.refractorredclient.clients.loggingmodel.ClientSubscribers;
import httpenums.HttpMethod;
import publishsubscribe.constants.Channels;
import publishsubscribe.constants.ErrorChannelConfigProp;
import publishsubscribe.errorsubscriberimpl.DefaultSubscriber;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;
import requests.bodyhandlers.StringBodyHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Home {
    public static void main(String[] args) throws MalformedURLException {
        Properties properties = new Properties();
        properties.put(ErrorChannelConfigProp.REQUEST_NOTIFICATION_FILE, "C:\\Users\\miko7\\IdeaProjects\\EasyHttp\\notification.txt");

        DefaultSubscriber subscriber = new DefaultSubscriber(properties);

        ClientSubscribers clientSubscribers = new ClientSubscribers();
        clientSubscribers.registerHttpNotificationChannel(Channels.REQUEST_NOTIFICATION, subscriber);

        LoggableClient client = LoggableClient.newBuilder()
                .subscribers(clientSubscribers)
                .buildNew();

        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setMethod(HttpMethod.GET)
                .setUri(new URL("https://jsonplaceholder.typicode.com/todos/1"))
                .build();

        EasyHttpResponse<String> response = client.sendAndLog(request, new StringBodyHandler());
    }

}
