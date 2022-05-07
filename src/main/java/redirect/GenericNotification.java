package redirect;

import Headers.HttpHeader;
import publishsubscribe.GenericCommunicate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class GenericNotification extends GenericCommunicate<GenericNotification> {
    private LocalDateTime dateTime;
    private String msg;
    private List<HttpHeader> responseHttpHeaders;
    private String resourceAddress;
    private NotificationTypes notificationType;


    public GenericNotification(LocalDateTime dateTime, String msg, List<HttpHeader> responseHttpHeaders, String resourceAddress, NotificationTypes notificationType){
        super();
        this.dateTime = dateTime;
        this.msg = msg;
        this.resourceAddress = resourceAddress;
        this.responseHttpHeaders = responseHttpHeaders;
        this.notificationType = notificationType;
    }

    public static String formattedGenericNotification(GenericNotification genericNotification){
        StringBuilder content = new StringBuilder();

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendChronologyText(TextStyle.FULL)
                .appendPattern("yyyy L E HH:mm:ss")
                .toFormatter(Locale.ENGLISH);

        String date = genericNotification.getDateTime().format(dateTimeFormatter);

        char newLine = '\n';

        content.append("Log date time: ").append(date).append(newLine);
        content.append("Notification msg: ").append(genericNotification.getMsg()).append(newLine);
        content.append("Response headers: ").append(newLine).append(newLine);
        for(HttpHeader httpHeader : genericNotification.getResponseHeaders()){
            content.append("header key: ")
                    .append(httpHeader.getKey())
                    .append("\n")
                    .append("header value: ")
                    .append(httpHeader.getValue())
                    .append("\n")
                    .append("------------------------------")
                    .append("\n");
        }

        return content.toString();
    }

    public NotificationTypes getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationTypes notificationType) {
        this.notificationType = notificationType;
    }

    public List<HttpHeader> getResponseHeaders() {
        return responseHttpHeaders;
    }

    public void setResponseHeaders(List<HttpHeader> responseHttpHeaders) {
        this.responseHttpHeaders = responseHttpHeaders;
    }

    public String getResourceAddress() {
        return resourceAddress;
    }

    public void setResourceAddress(String resourceAddress) {
        this.resourceAddress = resourceAddress;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
