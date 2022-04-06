package redirect;

import Headers.Header;
import publishsubscribe.GenericCommunicate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class GenericError extends GenericCommunicate {
    private int status;
    private String exceptionMg;
    private ErrorType errorType;
    private String serverMsg;
    private List<Header> responseHeaders;

    public GenericError(int status,
                        List<Header> responseHeaders,
                        String exceptionMg,
                        ErrorType errorType) {
        this.status = status;
        this.responseHeaders = responseHeaders;
        this.exceptionMg = exceptionMg;
        this.errorType = errorType;
    }
    public GenericError(int status,
                        List<Header> responseHeaders,
                        String exceptionMg,
                        ErrorType errorType,
                        String serverMsg) {
        this.status = status;
        this.responseHeaders = responseHeaders;
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
        content.append("Response headers: ").append(genericError.getResponseHeaders()).append(newLine).append(newLine);

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

    public List<Header> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(List<Header> responseHeaders) {
        this.responseHeaders = responseHeaders;
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
                ", responseHeaders=" + responseHeaders +
                '}';
    }
}
