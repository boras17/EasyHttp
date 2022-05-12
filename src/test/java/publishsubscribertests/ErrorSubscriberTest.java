package publishsubscribertests;

import headers.HttpHeader;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import publishsubscribe.ChannelMessageType;
import publishsubscribe.Operation;
import publishsubscribe.communcates.notifications.GenericAppError;
import publishsubscribe.communcates.notifications.GenericHttpNotification;
import publishsubscribe.constants.Channels;
import publishsubscribe.constants.ErrorChannelConfigProp;
import publishsubscribertests.testsubscribers.AppErrorSubscriber;
import publishsubscribertests.testsubscribers.RequestNotificationSubscriber;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ErrorSubscriberTest {
    @Test
    public void givenGenericAppErrorToDefaultSubscriberShouldSaveErrorInFile() throws IOException {
        //given
        Properties properties = new Properties();
        String logFilePath = "C:\\Users\\miko7\\IdeaProjects\\EasyHttp\\src\\test\\java\\publishsubscribertests\\logfiles\\apperrorfile.txt";
        properties.put(ErrorChannelConfigProp.APP_ERROR_FILE, logFilePath);
        AppErrorSubscriber defaultSubscriber = new AppErrorSubscriber(properties);

        //when
        Operation operation = new Operation();
        operation.subscribe(Channels.APP_ERROR_CHANNEL, defaultSubscriber);
        operation.publish(Channels.APP_ERROR_CHANNEL, new GenericAppError(new Exception("hello world"), ChannelMessageType.APP));

        //then
        File file = new File(logFilePath);
        BufferedReader bufferedReader = new BufferedReader( new FileReader(file));
        String loggedErrorLine = bufferedReader.readLine();
        String expectedLog = "hello world";

        Assertions.assertEquals(expectedLog,loggedErrorLine);
    }

    @Test
    public void givenGenericHttpNotificationToSubscriberShouldLogIntoNotificationFile() throws IOException {
        //given
        Properties properties = new Properties();
        String logFilePath = "C:\\Users\\miko7\\IdeaProjects\\EasyHttp\\src\\test\\java\\publishsubscribertests\\logfiles\\requestnotificationfile.txt";
        properties.put(ErrorChannelConfigProp.REQUEST_NOTIFICATION_FILE, logFilePath);
        RequestNotificationSubscriber defaultSubscriber = new RequestNotificationSubscriber(properties);

        //when
        List<HttpHeader> responseHeaders = new ArrayList<>();
        HttpHeader header = new HttpHeader();
        header.setKey("accept");
        header.setValue("application/json");
        responseHeaders.add(header);
        Operation operation = new Operation();
        operation.subscribe(Channels.REQUEST_NOTIFICATION, defaultSubscriber);
        operation.publish(Channels.REQUEST_NOTIFICATION, new GenericHttpNotification(LocalDateTime.of(2022,11,20,13,20,20,20),"Request registered", responseHeaders, "http://localhost:4545/users",ChannelMessageType.NOTIFICATION));

        //then
        File file = new File(logFilePath);
        FileInputStream fileReader = new FileInputStream(file);

        File expectedFile = new File("C:\\Users\\miko7\\IdeaProjects\\EasyHttp\\src\\test\\java\\publishsubscribertests\\logfiles\\expectedrequestnotification.txt");
        FileInputStream expectedStream = new FileInputStream(expectedFile);

        String logged = new String(fileReader.readAllBytes());
        String expected = new String(expectedStream.readAllBytes());
        Assertions.assertEquals(expected.replaceAll("\\s+",""), logged.replaceAll("\\s+",""));
    }
}
