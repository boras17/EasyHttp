package declarativeclient;

import client.EasyHttpClient;
import client.clients.builders.DefaultClientBuilder;

import java.io.IOException;
import java.lang.reflect.Proxy;

public class DeclarativeClientParser<T> {

    private Class<T> aClass;
    private EasyHttpClient client;

    public DeclarativeClientParser(Class<T> aClass) throws IOException {
        this(aClass, new DefaultClientBuilder().build());
    }

    public DeclarativeClientParser(Class<T> aClass, EasyHttpClient client) {
        this.aClass = aClass;
        this.client = client;
    }

    public T getImplementation() throws IOException {
        DeclaredClientProxy declaredclientProxy = new DeclaredClientProxy(this.client);
        return (T) Proxy.newProxyInstance(aClass.getClassLoader(), new Class[]{aClass}, declaredclientProxy);
    }

}
