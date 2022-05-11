package publishsubscribe.communcates.notifications;

import headers.HttpHeader;
import publishsubscribe.communcates.GenericCommunicate;
import publishsubscribe.ChannelMessageType;

import java.nio.channels.Channel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class GenericHttpError extends GenericCommunicate<GenericHttpError> {
    private int status;
    private String exceptionMg;

    private String serverMsg;
    private List<HttpHeader> responseHttpHeaders;

    public GenericHttpError(int status,
                            List<HttpHeader> responseHttpHeaders,
                            String exceptionMg, ChannelMessageType channelMessageType) {
        this.status = status;
        this.responseHttpHeaders = responseHttpHeaders;
        this.exceptionMg = exceptionMg;
        super.setErrorType(channelMessageType);
    }

    public GenericHttpError(int status,
                            List<HttpHeader> responseHttpHeaders,
                            ChannelMessageType channelMessageType,
                            String msg,
                            String serverMsg) {
        this.status = status;
        this.responseHttpHeaders = responseHttpHeaders;
        super.setErrorType(channelMessageType);
        this.exceptionMg = msg;
        this.serverMsg = serverMsg;
    }

    public Optional<String> getServerMessage(){
        return Optional.ofNullable(this.serverMsg);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<HttpHeader> getResponseHeaders() {
        return responseHttpHeaders;
    }

    public void setResponseHeaders(List<HttpHeader> responseHttpHeaders) {
        this.responseHttpHeaders = responseHttpHeaders;
    }

    public String getMsg() {
        return exceptionMg;
    }


    public void setMsg(String msg) {
        this.exceptionMg = msg;
    }
    public void setServerMsg(String serverMsg){
        this.serverMsg = serverMsg;
    }

    @Override
    public String formatGenericCommunicate() {
        StringBuilder content = new StringBuilder();

        LocalDateTime localDateTime = LocalDateTime.now();

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendChronologyText(TextStyle.FULL)
                .appendPattern("yyyy L E HH:mm:ss")
                .toFormatter(Locale.ENGLISH);

        String date = localDateTime.format(dateTimeFormatter);

        char newLine = '\n';

        content.append("Log date time: ").append(date).append(newLine);
        content.append("Server response: ").append(this.getServerMessage().orElse("")).append(newLine);
        content.append("Exception msg: ").append(this.getMsg()).append(newLine);
        content.append("Response status: ").append(this.getStatus()).append(newLine);
        content.append("Response headers: ").append(newLine).append(newLine);
        for(HttpHeader httpHeader : this.getResponseHeaders()){
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
}
