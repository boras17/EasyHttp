package requests.multirpart.simplerequest.jsonsender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class BodyProvider<T>{
    private T request;
    private OutputStream outputStream;
    private byte[] content;

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

    public byte[] getInputStream() {
        return this.getInputStream();
    }

    public void setInputStream(byte[] bytes){
        this.content = bytes;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

}
