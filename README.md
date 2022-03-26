# EasyHttp
How to use?
Firstly you must create new instance of EasyHtpp. You can do this with EasyHttpBuilder. Example how to create new instance with builder:
```java 
    EasyHttp client = new EasyHttp.EasyHttpBuilder()
                .build();
```
EasyHTtpBuilder makes it possible among others to set user agent and authenticator for requests:
```java 
EasyHttp client = new EasyHttp.EasyHttpBuilder()
                .setAuthenticationProvider(someAuthenticationProvider)
                .setUserAgent(someUserAgent)
                .setCookieExtractor(cookie extractor)
                .build();
```
As you can see, EasyHttpBuilder provide mechanism for easy extracting cookies from response via "cookie extractor".
How to create cookie extractor? 
```java 
CookieExtractor cookieExtractor = new CookieExtractor();
        EasyHttp client = new EasyHttp.EasyHttpBuilder()
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
        
        EasyHttp client = new EasyHttp.EasyHttpBuilder()
                .setAuthenticationProvider(authenticationProvider)
                .build();
```
I thing it is very clear. There is AuthenticationProvider which is asbtract class and BasicAuthenticationProvider class which inherits from AuthenticationProvider. In AuthenticationProvider abstract class we have two parameters constructors which accepts two paramters. First parameter is username and second one is password.
Let's move on to Digest Authentication support for this purpose you can use DigestAuthenticaionProvider which have constructor with three parameters. First and second parameter wroks as same as in BasicAuthenticationProvider but the third parameter is pointer for instance of DigestConfigurationClass which provide extra data for this type of authentication.
```java
    DigestConfiguration digestConfiguration = new DigestConfiguration.DigestConfigBuilder()
        .setQop(qop)
        .setNonce(none)
        .setMethod(method) 
        .setRealm(realm)
        .setHashAlgorithm(alg)
        .setCnonce(cnonce)
        .build();

    AuthenticationProvider authenticationProvider = new DigestAuthenticationProvider("username","password", digestConfiguration);

    EasyHttp client = new EasyHttp.EasyHttpBuilder()
        .setAuthenticationProvider(authenticationProvider)
        .build();
```
Ok when we have configured Client the next step is creating requests. If you want create new request you can use EasyHttpRequest class and her bulder just like below:
```java
        EasyHttpRequest request = new EasyHttpRequest.EasyHttpRequestBuilder()
                .setUri(new URL("someurl"))
                .setMethod(Method.POST)
                .setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("178.212.54.137",8080)))
                .addHeader(new Header("name","value"))
                .build();
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
                .build();
                
MultipartBody multipartBody = new MultipartBody.MultiPartBodyBuilder()
                .addPart(new TextPart("Hello world","partName"))
                .build();
```
When you create MultipartBody it is time to pass it to MultipartBodyProvider as a constructor parameter:
```java
MultipartBodyProvider multipartBodyProvider = new MultipartBodyProvider(multipartBody);
```
