package redirect;

import Headers.Header;
import HttpEnums.Method;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RedirectionHandler {

    private final Map<Integer, List<Method>> saveMethodsForRedirect = null;

    private final RedirectSafety redirectSafety;

    public RedirectionHandler(RedirectSafety redirectSafety) {
        this.redirectSafety = redirectSafety;
    }

    public void modifyRequest(EasyHttpRequest request, EasyHttpResponse<?> response) throws MalformedURLException {
        int redirectStatus = response.getStatus();

        final List<Header> redirectHeaders = this.extractRedirectHeaders(response.getResponseHeaders());

        final Header locationHeader = response.getResponseHeaders()
                .stream()
                .filter(header -> header.getKey().equalsIgnoreCase("location"))
                .findFirst()
                .orElseThrow();

        final String resourceLocation = locationHeader.getValue();
        boolean isLocationURLAbsolute = URI.create(resourceLocation).isAbsolute();

        URL locationURL = new URL(resourceLocation);

        if(!isLocationURLAbsolute){
            locationURL = this.createLocationURL(request);
        }

        final Set<Method> saveMethods = this.getSaveMethodsForRedirect(redirectStatus);

        final Method requestMethod = request.getMethod();

        switch (this.redirectSafety){
            case SAFE -> {
                if(saveMethods.contains(requestMethod)){
                    request.setUrl(locationURL);
                }
            }
            case UN_SAFE -> {
                request.setUrl(locationURL);
            }
            case UN_SAFE_FOR_POST -> {
                if(requestMethod.equals(Method.POST) && saveMethods.contains(requestMethod)){
                    request.setUrl(locationURL);
                }
            }
            case UN_SAFE_FOR_GET -> {
                if(requestMethod.equals(Method.GET)){
                    request.setUrl(locationURL);
                }
            }
        }
    }

    /**
     * http client checks if server respond with redirect status if status server sent redirect
     * status then client should invoke modifyRequestMethod and send request again
     * @param response - server response
     * @param request - client request
     * @return return true if server sent redirect status otherwise returns false
     */
    public boolean checkIfCanBeRedirected(EasyHttpResponse<?> response, EasyHttpRequest request) {
        final int statusCode = response.getStatus();
        final Method method = request.getMethod();

        final Header locationHeader = response.getResponseHeaders()
                .stream()
                .filter(header -> header.getKey().equalsIgnoreCase("location"))
                .findFirst()
                .orElse(null);

        switch (statusCode) {
            case HttpURLConnection.HTTP_MOVED_TEMP -> {
                return canBeRedirected(method) && locationHeader != null;
            }
            case HttpURLConnection.HTTP_MOVED_PERM ->  {
                return canBeRedirected(method);
            }
            case HttpURLConnection.HTTP_SEE_OTHER -> {
                return true;
            }
            default ->{
                return false;
            }
        }
    }

    private boolean canBeRedirected(Method method){
        final Set<Method> redirectableMethods = RedirectUtils.getRedirectableMethods();

        for (final Method _method: redirectableMethods) {
            if (method.equals(method)) {
                return true;
            }
        }
        return false;
    }

    private URL createLocationURL(EasyHttpRequest request) throws MalformedURLException {
        URL resourceUrl = request.getUrl();

        String host = resourceUrl.getHost();
        String path = resourceUrl.getPath();
        String protocol = resourceUrl.getProtocol();

        int port = resourceUrl.getPort();

        String locationStr = protocol.concat("://").concat(host);

        if(port != -1){
            locationStr+=locationStr.concat(":").concat(String.valueOf(port));
        }

        locationStr = locationStr.concat(path);

        return new URL(locationStr);
    }

    private Set<Method> getSaveMethodsForRedirect(int responseStatus){
        return this.saveMethodsForRedirect
                .entrySet()
                .stream()
                .filter(integerListEntry -> {
                    return integerListEntry.getKey() == responseStatus;
                })
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<Header> extractRedirectHeaders(List<Header> headers){
        return headers.stream()
                .toList();
    }
}
