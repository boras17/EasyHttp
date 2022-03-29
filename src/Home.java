import publishsubscribe.Channels;
import publishsubscribe.errorsubscriberimpl.SaveSubscriber;
import publishsubscribe.errorsubscriberimpl.Subscriber;
import redirect.redirectexception.RedirectionUnhandled;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class Home {
    public static void main(String[] args) throws IOException, IllegalAccessException, RedirectionUnhandled {

        Map<String, Subscriber> map = Collections.singletonMap(Channels.ERROR_CHANNEL,new SaveSubscriber(Paths.get("")));

        EasyHttp easyHttp = new EasyHttp.EasyHttpBuilder()
                .subscribeForChannels(map)
                .build();

    }
}
