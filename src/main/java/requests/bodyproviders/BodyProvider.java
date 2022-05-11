package requests.bodyproviders;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BodyProvider<T>{
    private T request;
    private OutputStream outputStream;

    protected BodyProvider(T request){
        this.request = request;
    }

    public abstract void prepareAndCopyToStream() throws IllegalAccessException, IOException;

    public T getRequest() {
        return this.request;
    }

    public OutputStream getOutputStream() {
        return this.outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

}
