package redirect;

import Headers.HttpHeader;
import publishsubscribe.GenericCommunicate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class GenericError extends GenericCommunicate<GenericError> {
    private int status;
    private String exceptionMg;
    private ErrorType errorType;
    private String serverMsg;
    private List<HttpHeader> responseHttpHeaders;

    public GenericError(int status,
                        List<HttpHeader> responseHttpHeaders,
                        String exceptionMg,
                        ErrorType errorType) {
        this.status = status;
        this.responseHttpHeaders = responseHttpHeaders;
        this.exceptionMg = exceptionMg;
        this.errorType = errorType;
    }
    public GenericError(int status,
                        List<HttpHeader> responseHttpHeaders,
                        String exceptionMg,
                        ErrorType errorType,
                        String serverMsg) {
        this.status = status;
        this.responseHttpHeaders = responseHttpHeaders;
        this.exceptionMg = exceptionMg;
        this.errorType = errorType;
        this.serverMsg = serverMsg;
    }


    public static String formattedGenericError(GenericError genericError){
        StringBuilder content = new StringBuilder();

        LocalDateTime localDateTime = LocalDateTime.now();

        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendChronologyText(TextStyle.FULL)
                .appendPattern("yyyy L E HH:mm:ss")
                .toFormatter(Locale.ENGLISH);

        String date = localDateTime.format(dateTimeFormatter);

        char newLine = '\n';

        content.append("Log date time: ").append(date).append(newLine);
        content.append("Server response: ").append(genericError.getServerMessage().orElse("")).append(newLine);
        content.append("Exception msg: ").append(genericError.getMsg()).append(newLine);
        content.append("Response status: ").append(genericError.getStatus()).append(newLine);
        content.append("Response headers: ").append(newLine).append(newLine);
        for(HttpHeader httpHeader : genericError.getResponseHeaders()){
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

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public void setMsg(String msg) {
        this.exceptionMg = msg;
    }
    public void setServerMsg(String serverMsg){
        this.serverMsg = serverMsg;
    }
    @Override
    public String toString() {
        return "GenericError{" +
                "status=" + status +
                ", msg='" + exceptionMg + '\'' +
                ", errorType=" + errorType +
                ", responseHeaders=" + responseHttpHeaders +
                '}';
    }
}
