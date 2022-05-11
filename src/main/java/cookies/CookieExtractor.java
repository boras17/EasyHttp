package cookies;

import headers.HttpHeader;
import requests.EasyHttpResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CookieExtractor {

    private List<Cookie> cookies;
    //example cookie response


    public List<Cookie> getCookies(){
        return this.cookies;
    }

    public void setCookies(EasyHttpResponse<?> response) {
        List<HttpHeader> responseHttpHeaders = response.getResponseHeaders();
        this.cookies = responseHttpHeaders
                .stream()
                .filter(x -> Optional.ofNullable(x.getKey()).orElse("").equals("Set-Cookie"))
                .map((cookie) -> {
                    System.out.println(cookie);
                    String[] cookieParts = cookie.getValue().split(";[ ]?");
                    Map<String, String> cookiePartsMap = new LinkedHashMap<>();
                    for(String cookiePart: cookieParts) {
                        String[] equalsSplit = cookiePart.split("=");
                        int arrLen = equalsSplit.length;
                        if(arrLen >= 2){
                            cookiePartsMap.put(equalsSplit[0], Optional.ofNullable(equalsSplit[1]).orElse(""));
                        }else{
                            cookiePartsMap.put(equalsSplit[0], "");
                        }
                    }
                    return cookiePartsMap;
                }).map(cookiePartsMap -> {
                    Cookie cookie = new Cookie();
                    // setting cookie name and vlaue
                    String cookieName = cookiePartsMap.keySet().iterator().next();
                    cookie.setCookieName(cookieName);
                    String cookieValue = cookiePartsMap.get(cookieName);
                    cookie.setCookieValue(cookieValue);
                    // setting expires at
                    Optional.ofNullable(cookiePartsMap.get("Expires"))
                            .ifPresent(cookieExpires -> {
                                //Mon, 21-Mar-2022 06:39:21 GMT
                                DateTimeFormatter dateFormat = new DateTimeFormatterBuilder()
                                        .appendPattern("E, dd MMM yyyy HH:mm:ss 'GMT'")
                                        .toFormatter(Locale.ENGLISH);
                                System.out.println("cookie expires"+ cookieExpires);
                                cookie.setExpiresAt(LocalDateTime.parse(cookieExpires,dateFormat));
                            });
                    // setting domain
                    Optional.ofNullable(cookiePartsMap.get("Domain"))
                            .ifPresent(cookie::setDomain);
                    // setting max age
                    Optional.ofNullable(cookiePartsMap.get("Max-Age"))
                            .ifPresent(maxAge -> cookie.setMaxAge(Integer.parseInt(maxAge)));
                    // http secure
                    Optional<?> secured = Optional.ofNullable(cookiePartsMap.get("Secure"));
                    cookie.setSecured(secured.isPresent());
                    System.out.println("secured: " + secured.isPresent());
                    // http only
                    Optional<?> httpOnly = Optional.ofNullable(cookiePartsMap.get("HttpOnly"));
                    cookie.setHttpOnly(httpOnly.isPresent());
                    return cookie;
                }).collect(Collectors.toList());
    }
    /**
     * key: Set-Cookie
     * cookie2=helloworld
     * cookie1=witajswiecie; Domain=somedomainn
     */
    Function<String, Cookie> cookieMapper ()  {
        return (entry) -> {
            String[] bodyParts = entry.split("; ");
            for(String s: bodyParts) {
                System.out.println("s: " + s);
            }
            return new Cookie.CookieBuilder()
                    .build();
        };
    }

    public Cookie cookieParser() {
        return null;
    }
}
