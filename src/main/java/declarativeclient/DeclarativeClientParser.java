package declarativeclient;

import client.refractorredclient.EasyHttpClient;
import client.refractorredclient.clients.DefaultClient;
import client.refractorredclient.clients.builders.DefaultClientBuilder;
import requests.EasyHttpResponse;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.function.Consumer;

public class DeclarativeClientParser<T> {

    private Class<T> aClass;
    private Consumer<EasyHttpResponse<?>> responseConsumer;
    private DeclaredClientProxy declaredClientProxy;
    private EasyHttpClient client;

    public DeclarativeClientParser(Class<T> aClass) throws IOException {
        this(aClass, new DefaultClientBuilder().build());
    }

    public DeclarativeClientParser(Class<T> aClass, EasyHttpClient client) {
        this.aClass = aClass;
        this.client = client;
    }


    public void addResponseMetadataHandler(Consumer<EasyHttpResponse<?>> responseConsumer){
        this.responseConsumer = responseConsumer;
    }

    public T getImplementation() throws IOException {
        DeclaredClientProxy declaredclientProxy = new DeclaredClientProxy(this.client, this.responseConsumer);
        return (T) Proxy.newProxyInstance(aClass.getClassLoader(), new Class[]{aClass}, declaredclientProxy);
    }

}
