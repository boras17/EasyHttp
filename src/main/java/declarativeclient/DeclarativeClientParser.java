package declarativeclient;

import client.EasyHttp;
import client.EasyHttpBuilder;
import requests.easyresponse.EasyHttpResponse;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

public class DeclarativeClientParser<T> {

    private Class<T> aClass;
    private Consumer<EasyHttpResponse<?>> responseConsumer;

    public DeclarativeClientParser(Class<T> aClass){
        this.aClass = aClass;

    }

    public void addResponseMetadataHandler(Consumer<EasyHttpResponse<?>> responseConsumer){
        this.responseConsumer = responseConsumer;
    }

    public T getImplementation() throws IOException {
        DeclaredclientProxy declaredclientProxy = new DeclaredclientProxy(new EasyHttpBuilder().build(), this.responseConsumer);
        return (T) Proxy.newProxyInstance(aClass.getClassLoader(), new Class[]{aClass}, declaredclientProxy);
    }

}
