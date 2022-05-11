package redirect;

import headers.HttpHeader;
import httpenums.HttpMethod;
import publishsubscribe.communcates.notifications.GenericHttpError;
import redirect.redirectexception.RedirectWithoutLocationException;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.EasyHttpResponse;
import requests.EasyHttpRequest;

import java.net.*;

import java.util.Set;


public class RedirectionHandler {
    private final Set<HttpMethod> redirectableHttpMethods;
    private final RedirectSafety redirectSafety;

    public RedirectionHandler(final Set<HttpMethod> redirectableHttpMethods, RedirectSafety redirectSafety){
        this.redirectableHttpMethods = redirectableHttpMethods;
        this.redirectSafety = redirectSafety;
    }

    public RedirectionHandler(final Set<HttpMethod> redirectableHttpMethods){
        this(redirectableHttpMethods, RedirectSafety.ALWAYS);
    }

    public void modifyRequest(final EasyHttpRequest request,
                              final EasyHttpResponse<?> response) throws
            UnsafeRedirectionException,
            RedirectionCanNotBeHandledException,
            MalformedURLException {

        int responseStatus = response.getStatus();
        final HttpHeader locationHttpHeader = response.getResponseHeaders()
                .stream()
                .filter(header -> header.getKey().equalsIgnoreCase("location"))
                .findFirst()
                .orElseThrow(() -> new RedirectWithoutLocationException("Redirection occurred but there is no Location"));

        final String resourceLocation = locationHttpHeader.getValue();
        final boolean isLocationURLAbsolute = URI.create(resourceLocation).isAbsolute();

        try{
            URL locationURL = new URL(resourceLocation);

            if(locationURL.getProtocol().equals("http") &&
                    this.redirectSafety.equals(RedirectSafety.ALWAYS)){
                String msg = "Redirection blocked because switching to http from https occurred";
                //TODO
                //GenericHttpError error = new GenericHttpError(responseStatus,response.getResponseHeaders(),msg);
                //throw new UnsafeRedirectionException(error);
            }

            if(!isLocationURLAbsolute){
                URL requestUrl = request.getUrl();
                locationURL = this.createLocationURL(requestUrl, resourceLocation);
            }

            boolean isRedirectable = this.checkIfRedirectCanBeHandled(response, request) &&
                    this.redirectSafety.equals(RedirectSafety.ALWAYS);

            if(isRedirectable){
                request.setUrl(locationURL);
            }else{
                // TODO
                //GenericHttpError genericHttpError = new GenericHttpError(responseStatus,
                //        response.getResponseHeaders(),"Unsuccessful attempting to handle redirect");
                //throw new RedirectionCanNotBeHandledException(genericHttpError);
            }
        }catch (MalformedURLException e){
            throw new MalformedURLException(e.getMessage());
        }
    }

    /**
     * http client checks if server respond with redirect status if status server sent redirect
     * status then client should invoke modifyRequestMethod and send request again
     * @param response - server response
     * @param request - client request
     * @return return true if server sent redirect status otherwise returns false
     */
    private boolean checkIfRedirectCanBeHandled(final EasyHttpResponse<?> response,
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

    private boolean isRedirectable(final HttpMethod httpMethod) {
        return this.redirectableHttpMethods.contains(httpMethod);
    }

    private URL createLocationURL(final URL requestUrl, final String resourceLocation) throws MalformedURLException {
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
}
