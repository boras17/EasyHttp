import HttpEnums.Method;
import client.EasyHttp;
import jsonoperations.JsonCreator;
import publishsubscribe.Channels;
import publishsubscribe.errorsubscriberimpl.ErrorSubscriber;
import publishsubscribe.errorsubscriberimpl.Subscriber;
import redirect.redirectexception.RedirectionUnhandled;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class Home {
    public static class User {
        private Integer id;
        private String username;
        private List<String> userSkills;
        private Boolean alive;

        public User(int id, String username, List<String> userSkills, boolean alive) {
            this.id = id;
            this.username = username;
            this.userSkills = userSkills;
            this.alive = alive;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public List<String> getUserSkills() {
            return userSkills;
        }

        public void setUserSkills(List<String> userSkills) {
            this.userSkills = userSkills;
        }

        public boolean isAlive() {
            return alive;
        }

        public void setAlive(boolean alive) {
            this.alive = alive;
        }
    }

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


        User user = new User(123,"Ben",List.of("One","Two"),true);
        JsonCreator jsonCreator = new JsonCreator();
        jsonCreator.generateJson(user);
        System.out.println(jsonCreator.getJson());


    }
}
