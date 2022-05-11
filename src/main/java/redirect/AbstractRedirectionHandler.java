package redirect;

import headers.HttpHeader;
import httpenums.HttpMethod;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public abstract class AbstractRedirectionHandler {
    private Set<HttpMethod> redirectableHttpMethods;
    private RedirectSafety redirectSafety;

    public AbstractRedirectionHandler(final Set<HttpMethod> redirectableHttpMethods, RedirectSafety redirectSafety){
        this.redirectableHttpMethods = redirectableHttpMethods;
        this.redirectSafety = redirectSafety;
    }
    public AbstractRedirectionHandler() {}

    public abstract void modifyRequest(final EasyHttpRequest request, final EasyHttpResponse<?> response) throws MalformedURLException, RedirectionCanNotBeHandledException, UnsafeRedirectionException;

    protected boolean checkIfRedirectCanBeHandled(final EasyHttpResponse<?> response,
                                                final EasyHttpRequest request) {
        final int statusCode = response.getStatus();
        final HttpMethod httpMethod = request.getMethod();

        final HttpHeader locationHttpHeader = response.getResponseHeaders()
                .stream()
                .filter(header -> header.getKey().equalsIgnoreCase("location"))
                .findFirst()
                .orElse(null);

        return switch (statusCode){
            case HttpURLConnection.HTTP_MOVED_PERM, 307,
                    HttpURLConnection.HTTP_MOVED_TEMP -> isRedirectable(httpMethod) && locationHttpHeader !=null;
            case HttpURLConnection.HTTP_SEE_OTHER -> isRedirectable(httpMethod) && httpMethod.equals(HttpMethod.GET);
            default -> false;
        };
    }

    protected boolean isRedirectable(final HttpMethod httpMethod) {
        return this.redirectableHttpMethods.contains(httpMethod);
    }

    protected URL createLocationURL(final URL requestUrl, final String resourceLocation) throws MalformedURLException {
        final String host = requestUrl.getHost();
        final String protocol = requestUrl.getProtocol();

        int port = requestUrl.getPort();

        String locationStr = protocol.concat("://").concat(host);

        if(port != -1){
            locationStr+=locationStr.concat(":").concat(String.valueOf(port));
        }

        locationStr = locationStr.concat(resourceLocation.startsWith("/") ? "" : "/").concat(resourceLocation);

        return new URL(locationStr);
    }

    public Set<HttpMethod> getRedirectableHttpMethods() {
        return redirectableHttpMethods;
    }

    public RedirectSafety getRedirectSafety() {
        return redirectSafety;
    }
}
