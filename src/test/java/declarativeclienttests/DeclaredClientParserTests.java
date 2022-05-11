package declarativeclienttests;

import parts.FilePart;
import parts.Part;
import parts.PartType;
import requests.EasyHttpRequest;
import requests.bodyproviders.BodyProvider;
import requests.bodyproviders.JsonBodyProvider;
import requests.bodyproviders.MultipartBodyProvider;
import declarativeclient.DeclaredClientProxy;
import declarativeclient.MethodAndPath;
import declarativeclient.declarativeannotations.*;

import declarativeclient.declarativeannotations.httprequests.Get;
import declarativeclient.declarativeannotations.httprequests.Post;
import declarativeclient.declarativeclienterrors.CouldNotGuessBodyHandlerException;
import declarativeclient.declarativeclienterrors.MethodAndPathAreRequired;
import headers.HttpHeader;
import httpenums.HttpMethod;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import requests.easyrequest.MultipartBody;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@PrepareForTest({DeclaredClientProxy.class, MethodAndPath.class})
@RunWith(PowerMockRunner.class)
public class DeclaredClientParserTests {

    private interface TestMethods{
        @Header(key = "content-type", value = "application/json")
        void sendRequestWithHeader();

        @Headers(headers = {
                @Header(key = "content-type", value = "application/json"),
                @Header(key = "connection", value = "keep-alive")
        })
        void multiHeadersRequest();

        @Get("http://localhost:4545/person")
        void methodWithRequestParameters(@RequestParam("name") String name, @RequestParam("sort") String sort);

        @RequestProxy(proxyServer = @ProxyHostAndPort(host = "localhost", port = 8080),
                      type = Proxy.Type.HTTP)
        void requestWithViaProxy();


        @Post("http://localhost:7777/users/{id}/orders")
        void methodWithPostAnnotation(@PathVariable("id") int id, @RequestParam("page") int page);
    }
    private class Person{
        String username; String password;

        public Person(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
    private interface TestMethodsForConstructingRequests{

        @RequestProxy(proxyServer = @ProxyHostAndPort(host = "localhost", port = 8080),
                type = Proxy.Type.HTTP)
        @Header(key = "accept", value = "application/json")
        @Get("http://localhost:4545/users/{id}/orders")
        void requestWithHeaderParamsAndProxy(@RequestParam("sort") String sort, @PathVariable("id") int id);

        @Post("http://localhost:5656/image")
        void uploadMultipartImages(@Multipart MultipartBody images);

        @Post("http://localhost:5656/users")
        void addNewUser(@RequestJsonBody Person person);
    }


    @Test
    public void givenMethodWith_Header_ShouldReturnHeader() throws Exception {
        Header header = TestMethods.class.getMethod("sendRequestWithHeader").getAnnotation(Header.class);

        DeclaredClientProxy proxy = PowerMockito.mock(DeclaredClientProxy.class);

        PowerMockito.when(proxy, "calculateHeader", Mockito.isA(Header.class))
                .thenCallRealMethod();

        HttpHeader httpHeader = WhiteboxImpl.invokeMethod(proxy, "calculateHeader", header);

        Assertions.assertThat(httpHeader)
                .has(new Condition<>(){
                    @Override
                    public boolean matches(HttpHeader value) {
                        return value.getKey().equals("content-type") &&
                                value.getValue().equals("application/json");
                    }
                });
   }

   @Test
    public void givenMethod_With_HeadersAnnotationShouldReturnListOfHeaders() throws Exception {
        Headers headers = TestMethods.class.getMethod("multiHeadersRequest").getAnnotation(Headers.class);

        DeclaredClientProxy proxy = PowerMockito.mock(DeclaredClientProxy.class);
        PowerMockito.when(proxy, "calculateHeaders", Mockito.isA(Headers.class)).thenCallRealMethod();
        PowerMockito.when(proxy, "calculateHeader", Mockito.isA(Header.class)).thenCallRealMethod();

        List<HttpHeader> headerList = WhiteboxImpl.invokeMethod(proxy, "calculateHeaders", headers);
        PowerMockito.verifyPrivate(proxy, Mockito.times(2)).invoke("calculateHeader", Mockito.any(Header.class));

        int headers_count = headerList.size();

        Assertions.assertThat(headers_count).isEqualTo(2);
        HttpHeader first_expected = headerList.get(0),
                    second_expected = headerList.get(1);
        Assertions.assertThat(first_expected)
                .is(new Condition<>(){
                    @Override
                    public boolean matches(HttpHeader value) {
                        return value.getKey().equals("content-type")
                                && value.getValue().equals("application/json");
                    }
                });
        Assertions.assertThat(second_expected)
                .is(new Condition<>(){
                    @Override
                    public boolean matches(HttpHeader value) {
                        return value.getKey().equals("connection")
                                && value.getValue().equals("keep-alive");
                    }
                });

   }

    @Test
    public void givenMethodWithRequestParamsShouldAddParamsToPath() throws Exception {
        Method methodWithRequestParams = TestMethods.class.getMethod("methodWithRequestParameters", String.class, String.class);
        DeclaredClientProxy declaredClientProxy = PowerMockito.mock(DeclaredClientProxy.class);

        PowerMockito.when(declaredClientProxy, "addRequestParameters",  Mockito.anyString(), Mockito.any(Map.class))
                .thenCallRealMethod();

        PowerMockito.when(declaredClientProxy,
            "createAnnotationParametersMap",
                        Mockito.any(Method.class),
                        Mockito.isA(Object[].class),
                        Mockito.any(Class.class))
                    .thenCallRealMethod();

        Map<RequestParam, Object> params_values = WhiteboxImpl.invokeMethod(declaredClientProxy,"createAnnotationParametersMap", methodWithRequestParams, new Object[]{"one","two"}, RequestParam.class);

        Get pathWithoutRequestParameters = methodWithRequestParams.getAnnotation(Get.class);
        String pathWithoutRequestParametersValue = pathWithoutRequestParameters.value();

        String pathWithRequestParams = WhiteboxImpl.invokeMethod(declaredClientProxy, "addRequestParameters",  pathWithoutRequestParametersValue, params_values);

        String expectedPath = "http://localhost:4545/person?name=one&sort=two";

        Assertions.assertThat(pathWithRequestParams).isEqualTo(expectedPath);
    }

    @Test
    public void givenMethodWithRequestProxyShouldReturnProxy() throws Exception {
        Method method = TestMethods.class.getMethod("requestWithViaProxy");
        DeclaredClientProxy declaredClientProxy = PowerMockito.mock(DeclaredClientProxy.class);

        PowerMockito.when(declaredClientProxy,"getProxyFromAnnotation", Mockito.any(Method.class))
                .thenCallRealMethod();

        Proxy proxy = WhiteboxImpl.invokeMethod(declaredClientProxy, "getProxyFromAnnotation",method);

        Assertions.assertThat(proxy)
                .has(new Condition<>(){
                    @Override
                    public boolean matches(Proxy value) {
                        InetSocketAddress inetSocketAddress = (InetSocketAddress)value.address();
                        return inetSocketAddress.getPort() == 8080
                                &&
                                inetSocketAddress.getHostString().equals("localhost")
                                &&
                                value.type().equals(Proxy.Type.HTTP);
                    }
                });
    }


    @Test
    public void givenMethodWithPostAnnotationShouldMethodAndPathObject() throws Exception {
        Method methodWithPostAnnotation = TestMethods.class.getMethod("methodWithPostAnnotation", int.class, int.class);
        DeclaredClientProxy declaredClientProxy = PowerMockito.mock(DeclaredClientProxy.class);

        PowerMockito.when(declaredClientProxy, "getHttpMethodAndPath", Mockito.any(Method.class),Mockito.anyMap(), Mockito.anyMap())
                .thenCallRealMethod();
        PowerMockito.when(declaredClientProxy, "createAnnotationParametersMap", Mockito.any(Method.class),
                Mockito.any(Object[].class), Mockito.any(Class.class)).thenCallRealMethod();
        PowerMockito.when(declaredClientProxy, "addPathVariables", Mockito.anyString(), Mockito.anyMap())
                .thenCallRealMethod();
        PowerMockito.when(declaredClientProxy, "addRequestParameters", Mockito.anyString(), Mockito.anyMap())
                .thenCallRealMethod();
        Map<PathVariable, Object> pathVariables = WhiteboxImpl.invokeMethod(declaredClientProxy, "createAnnotationParametersMap",
                methodWithPostAnnotation, new Object[]{1,2}, PathVariable.class);
        Map<RequestParam, Object> requestParameters = WhiteboxImpl.invokeMethod(declaredClientProxy, "createAnnotationParametersMap",
                methodWithPostAnnotation, new Object[]{1,2}, RequestParam.class);

        MethodAndPath methodAndPath = WhiteboxImpl.invokeMethod(declaredClientProxy, "getHttpMethodAndPath", methodWithPostAnnotation, pathVariables, requestParameters);
        Assertions.assertThat(methodAndPath)
                .has(new Condition<>() {
                    @Override
                    public boolean matches(MethodAndPath value) {
                        return value.getPath().equals("http://localhost:7777/users/1/orders?page=2")
                                && value.getMethod().equals(HttpMethod.POST);
                    }
                });
    }

    @Test
    public void givenRequestWithHeaderParamsAndProxyConstructRequest() throws Exception {
        Method requestWithHeaderParamsAndProxy = TestMethodsForConstructingRequests.class.getMethod("requestWithHeaderParamsAndProxy", String.class, int.class);

        DeclaredClientProxy declaredClientProxy = PowerMockito.spy(new DeclaredClientProxy());

        EasyHttpRequest easyHttpRequest = declaredClientProxy.calculateRequest(requestWithHeaderParamsAndProxy, new Object[]{"asc", 1});

        Assertions.assertThat(easyHttpRequest)
                .has(new Condition<>(){
                    @Override
                    public boolean matches(EasyHttpRequest value) {
                        int headersSize = value.getHeaders().size();
                        HttpHeader header = value.getHeaders().get(0);
                        return headersSize == 1
                                &&
                                header.getKey().equals("accept")
                                &&
                                header.getValue().equals("application/json");
                    }
                })
                .has(new Condition<>(){
                    @Override
                    public boolean matches(EasyHttpRequest value) {
                        Optional<Proxy> proxyOptional = easyHttpRequest.getProxy();
                        boolean proxyPresent = proxyOptional.isPresent();
                        if(proxyPresent){
                            Proxy proxy = proxyOptional.get();
                            InetSocketAddress sockAddr = (InetSocketAddress)proxy.address();
                            int port = sockAddr.getPort();
                            Proxy.Type type = proxy.type();
                            String host = sockAddr.getHostString();;
                            return type.equals(Proxy.Type.HTTP) && port == 8080 && host.equals("localhost");
                        }
                        return false;
                    }
                }).has(new Condition<>() {
                    @Override
                    public boolean matches(EasyHttpRequest value) {
                        String expectedUrl = "http://localhost:4545/users/1/orders?sort=asc";
                        String url = value.getUrl().toString();
                        return url.equals(expectedUrl);
                    }
                });

    }

    @Test
    public void givenRequestWithMultipartBodyShouldReturnRequestWithMultipartBody() throws NoSuchMethodException, IOException {
        Method requestWithHeaderParamsAndProxy = TestMethodsForConstructingRequests.class.getMethod("uploadMultipartImages", MultipartBody.class);

        File multipartFile = new File("multipartfile.txt");
        DeclaredClientProxy declaredClientProxy = PowerMockito.spy(new DeclaredClientProxy());

        MultipartBody multipartBody = new MultipartBody.MultiPartBodyBuilder()
                .addPart(new FilePart(multipartFile, "somepart"))
                .encoding(StandardCharsets.UTF_8.name())
                .setPartType(PartType.FILE)
                .build();

        EasyHttpRequest easyHttpRequest = declaredClientProxy.calculateRequest(requestWithHeaderParamsAndProxy, new Object[]{multipartBody});

        Assertions.assertThat(easyHttpRequest)
                .has(new Condition<>() {
                        @Override
                        public boolean matches(EasyHttpRequest value) {
                            Optional<BodyProvider<?>> multipartBody = easyHttpRequest.getBody();
                            boolean bodyProviderPresent = multipartBody.isPresent();
                            if(bodyProviderPresent) {
                                BodyProvider<?> bodyProvider = multipartBody.get();
                                boolean isMultipartProvider = bodyProvider.getClass().isAssignableFrom(MultipartBodyProvider.class);
                                if(isMultipartProvider){
                                    MultipartBodyProvider multipartBodyProvider = (MultipartBodyProvider) bodyProvider;
                                    MultipartBody multiPartBody = multipartBodyProvider.getRequest();

                                    List<Part> parts = multiPartBody.getParts();
                                    int partsSize = parts.size();
                                    if(partsSize == 1){
                                        Part part = parts.get(0);
                                        boolean isFilePart = part instanceof FilePart;
                                        if(isFilePart){
                                            FilePart filePart = (FilePart)part;
                                            File file = filePart.getFile();
                                            String partName = filePart.getPartName();
                                            return partName.equals("somepart") && file.getName().equals("multipartfile.txt");
                                        }
                                    }
                                }
                            }
                            return false;
                        }
                 }).has(new Condition<>(){
                    @Override
                    public boolean matches(EasyHttpRequest value) {
                        String url = value.getUrl().toString();
                        String expectedUrl = "http://localhost:5656/image";
                        return url.equals(expectedUrl);
                    }
                });
    }


    @Test
    public void givenRequestWithJsonBodyProviderShouldCorrectlyParseEntity() throws NoSuchMethodException, IOException {
        Method addNewUser = TestMethodsForConstructingRequests.class.getMethod("addNewUser", Person.class);
        Person testPerson = new Person("Adam", "Kowalski");
        DeclaredClientProxy declaredClientProxy = PowerMockito.spy(new DeclaredClientProxy());

        EasyHttpRequest request = declaredClientProxy.calculateRequest(addNewUser, new Object[]{testPerson});

        Assertions.assertThat(request)
                .has(new Condition<>(){
                    @Override
                    public boolean matches(EasyHttpRequest value) {
                        Optional<BodyProvider<?>> bodyProviderOptional = value.getBody();
                        boolean bodyProviderPresent = bodyProviderOptional.isPresent();
                        if(bodyProviderPresent){
                            BodyProvider<?> bodyProvider = bodyProviderOptional.get();
                            boolean isJsonProvider = bodyProvider.getClass().isAssignableFrom(JsonBodyProvider.class);
                            if(isJsonProvider) {
                                JsonBodyProvider jsonBodyProvider = (JsonBodyProvider) bodyProvider;
                                try {
                                    String json = WhiteboxImpl.invokeMethod(jsonBodyProvider, "getJson");
                                    String expectedJson = "{\"username\":\"Adam\",\"password\":\"Kowalski\"}";
                                    return json.equals(expectedJson);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return true;
                            }
                        }
                        return false;
                    }
                });
    }

    private interface InvalidDeclarations{
        void getData();
        @Get("http://localhost:3434/hello")
        Person methodWithInvalidBodyProvider();
    }

    @Test(expected = MethodAndPathAreRequired.class)
    public void givenMethodWithoutMethodAndPathShouldThrow() throws NoSuchMethodException, IOException {
        Method getData = InvalidDeclarations.class.getMethod("getData");
        DeclaredClientProxy declaredClientProxy = PowerMockito.spy(new DeclaredClientProxy());
        declaredClientProxy.calculateRequest(getData, new Object[]{});
    }
    @Test(expected = CouldNotGuessBodyHandlerException.class)
    public void givenMethodWithoutMethodAndPathShouldThrowRequestObjectRequiredException() throws Exception {
        Method getData = InvalidDeclarations.class.getMethod("methodWithInvalidBodyProvider");
        DeclaredClientProxy declaredClientProxy = PowerMockito.spy(new DeclaredClientProxy());
        declaredClientProxy.calculateRequest(getData, new Object[]{});
        WhiteboxImpl.invokeMethod(declaredClientProxy,"guessBodyHandler", getData.getClass());
    }
}
