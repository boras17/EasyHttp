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
