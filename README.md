# EasyHttp - It was created in order to boost my skills in java mechanizms and http mechanisms
How to use?
Firstly you must create new instance of EasyHtpp. You can do this with EasyHttpBuilder. Example how to create new instance with builder:
```java 
EasyHttp client = new client.EasyHttp.EasyHttpBuilder().build();
```
EasyHTtpBuilder makes it possible among others to set user agent and authenticator for requests:
```java 
EasyHttp client = new client.EasyHttp.EasyHttpBuilder()
                .setAuthenticationProvider(someAuthenticationProvider)
                .setUserAgent(someUserAgent)
                .setCookieExtractor(cookie extractor)
                .build();
```
As you can see, EasyHttpBuilder provide mechanism for easy extracting cookies from response via "cookie extractor".
How to create cookie extractor? 
```java 
CookieExtractor cookieExtractor = new CookieExtractor();
        client.EasyHttp client = new client.EasyHttp.EasyHttpBuilder()
                .setCookieExtractor(cookieExtractor)
                .build();
```
As you can see it is very simple you only need create new instance of CookieExtractor and then paste it to .setCookieExtractor().
The question which propably is in yout mind is how can I extract this cookies? After making successed request you can call getCookies() which returns List of Cookie(it is class which represents single cookie)
```java 
List<Cookie> cookies = cookieExtractor.getCookies();
```
Now let me explain you how to use 'Authenticator'. We have three options. First one is basic auth:
```java
AuthenticationProvider authenticationProvider
        = new BasicAuthenticationProvider("username","password");

client.EasyHttp client = new client.EasyHttp.EasyHttpBuilder()
        .setAuthenticationProvider(authenticationProvider)
        .build();
```
I thing it is very clear. There is AuthenticationProvider which is asbtract class and BasicAuthenticationProvider class which inherits from AuthenticationProvider. In AuthenticationProvider abstract class we have two parameters constructors which accepts two paramters. First parameter is username and second one is password.

Ok when we have configured Client the next step is creating requests. If you want create new request you can use EasyHttpRequest class and her bulder just like below:
```java
EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
        .setUri(new URL("someurl"))
        .setMethod(Method.POST)
        .setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("178.212.54.137",8080)))
        .addHeader(new Header("name","value"))
        .build();
```
or create interface with interface like in Feign Client:
Declared Client example:
```java
interface Crud{
        @Get("https://jsonplaceholder.typicode.com/todos/{id}")
        String getToDoJson(@PathVariable("id") int id);
        
        @Get("http://localhost:4545/users")
        @Headers(headers = {@Header(key = "accept", value = "application/json")})
        @RequestProxy(proxyServer = @ProxyHostAndPort(host = "localhost", port = 2323), type = Proxy.Type.HTTP)
        String getJsonResponse(@RequestParam("page") int page);

        @Post("http://localhost:4545/users/{userId}/avatar")
        void uploadAvatar(@Multipart MultipartBody multipartBody);

        @Post("http://localhost:4545/users")
        void addNewUser(@RequestJsonBody User newUser);

        @Post("http://localhost:4545/users")
        void addNewUserViaBodyProvider(@RequestBodyProvider JsonBodyProvider userJson);
    }

Crud crud = new DeclarativeClientParser<>(Crud.class, client).getImplementation();
```
or you can use DefaultClient:
```java
Crud crud = new DeclarativeClientParser<>(Crud.class).getImplementation();
```
the builder make it possible to set Proxy, URL, Headers (yes you can invoke add header a lot of times or pass List of Header's), Http method which is delivered by 'Mothod' enum. Very important part of this section is BodyProvider which allow you to pass body for this request. I created a few diffrent body providers. First body provider allows to send json body. If you want send json body you have to specify what you want to send for example i want send json representation of my Person class instance: 
```java
public class Person{
    String name;
    String surname;

    public Person(String name, String surname){
        this.name = name;
        this.surname = surname;
    }
}
```
now let's create instance of this class:
```java
    Person person = new Person("Ben","Surname");
```
now when you have your body it is time to create instance of JsonBodyProvider and give him our body as a paramaeter of his constructor:
```java
    JsonBodyProvider jsonBodyProvider = new JsonBodyProvider(person);
```
and the final step is passing jsonBodyProvider to our Request:
```java
EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setBodyProvider(jsonBodyProvider)
                .build();
```
You can very easly send multipart request. Firstly you need to create MultipartBody class instance. Method addPart takes as parameter FilePart or TextPart. FilePart allows you sending files and TextPart allows sending text part:
```java
MultipartBody multipartBody = new MultipartBody.MultiPartBodyBuilder()
                .addPart(new FilePart(new File("file.txt"),"partName"))
                .setPartType(PartType.FILE)
                .build();
                
MultipartBody multipartBody = new MultipartBody.MultiPartBodyBuilder()
                .addPart(new TextPart("Hello world","partName"))
                .setPartType(PartType.TEXT)
                .build();
```
When you create MultipartBody it is time to pass it to MultipartBodyProvider as a constructor parameter:
```java
MultipartBodyProvider multipartBodyProvider = new MultipartBodyProvider(multipartBody);
```
As you already guessed the final step is handover multipartBodyProvider to EasyHttpRequest:
```java
EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setBodyProvider(multipartBodyProvider)
                .build();
```
If you want you can send text with simple TextBodyProvider:
```java
TextBodyProvider textBodyProvider = new TextBodyProvider("some text");
```
When you create request you can send it to server by calling send or sendAsync method which is provided by EasyClient. First parameter of method send/sendAsync is EasyHttpRequest object and second one is BodyHandler:

Now it is time to handle response from server. For handling responses you can use EasyHttpResponse class: 
```java
EasyHttpResponse<Void> response = client.send(request, new EmptyBodyHandler());
```
Body handler Allows you extrect body from response but if you for example send some post request for server and you do not expect any body you can use EmptyBodyHandler and type of response will be Void. I created a few body Handlers. First one is StringBodyHandler which allows you to extract body as a String from server:
```java
EasyHttpResponse<String> response = client.send(request, new StringBodyHandler());
        String body = response.getBody();
```
InpuStream body:
```java
EasyHttpResponse<InputStream> response = client.send(request, new StreamBodyHandler());
        InputStream content = response.getBody();
```
With response object you can get response status:
```java
HttpStatus status = response.getResponseStatus();
```
and list of headers from server:
```java
List<Header> status = response.getResponseHeaders();
Header header = status.get(0);
String headerKey = header.getKey();
String headerValue = header.getValue();
```
sendAsync method sending request asynchronously and returns CompleteableFuture:
```java
CompletableFuture<EasyHttpResponse<String>> response = client.sendAsync(request, new StringBodyHandler());
```

Now you can easly create an proxy in order to make declared in UserCrud interface requests via DeclarativeClientParser:
```java
UserCrud userCrud = new DeclarativeClientParser<>(UserCrud.class).getImplementation();
```
Loggable Client
for that purpose you have to pass Map to setSubscribedChannles. The key of the map entry is Channel name. You can choose five diffrent channels:
```java
Channels.SERVER_ERROR_CHANNEL;
Channels.CLIENT_ERROR_CHANNEL;
Channels.REDIRECT_ERROR_CHANNEL;
Channels.APP_ERROR_CHANNEL;
```
The names of these channels are very intuitive SERVER_ERROR_CHANNEL will handle server errors, CLIENT_ERROR_CHANNEL will handle client errors etc.
The most important thing of ErrorSubscriber is Properties object. In properties you have to specify the path to the file:
```java
Properties properties = new Properties();
properties.put(ErrorChannelConfigProp.REQUEST_NOTIFICATION_FILE, "C:\\Users\\miko7\\IdeaProjects\\EasyHttp\\notification.txt");
```
and then pass properties to DefaulSubscriber constructor:
```java
DefaultSubscriber subscriber = new DefaultSubscriber(properties);
```
When you already have Subscriber Instance you can register this subscriber via ClientSubscribers class:
```java
ClientSubscribers clientSubscribers = new ClientSubscribers();
clientSubscribers.registerHttpNotificationChannel(Channels.REQUEST_NOTIFICATION, subscriber);
```
In order to create Loggable client use LoggableClientDecorator:
```java
LoggableClientDecorator loggableClientDecorator =  new LoggableClientDecorator(DefaultClient.newBuilder().build());
loggableClientDecorator.configureClientSubscribers(clientSubscribers);

Crud crud = new DeclarativeClientParser<>(Crud.class,loggableClientDecorator).getImplementation();
```


// 50% TODO: it is good choose to allow handling multiple request interceptors and give them specified paths to handle
//TODO try to generify subscriber 
// todo more tests for error subscribe

//TODO digest authenitcation provider support for response interceptor
//Maybe it is good to provide some flag: isAfterChallenge
if(isAfterchallenge){
then beforeRequest)
}else{
    on401Response();
}
to do annotation based http client

to do declarable interfaces for making creud request

TODO: encoding request parameters
