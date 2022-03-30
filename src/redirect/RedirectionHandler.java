package redirect;

import Headers.Header;
import HttpEnums.Method;
import publishsubscribe.Channels;
import publishsubscribe.Event;
import publishsubscribe.communcates.ErrorCommunicate;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.easyresponse.EasyHttpResponse;
import requests.multirpart.simplerequest.EasyHttpRequest;

import java.net.*;

import java.util.Set;


public class RedirectionHandler {
    private final Set<Method> redirectableMethods;
    private final RedirectSafety redirectSafety;

    public RedirectionHandler(final Set<Method> redirectableMethods, RedirectSafety redirectSafety){
        this.redirectableMethods = redirectableMethods;
        this.redirectSafety = redirectSafety;
    }

    public RedirectionHandler(final Set<Method> redirectableMethods){
        this(redirectableMethods, RedirectSafety.ALWAYS);
    }

    public void modifyRequest(final EasyHttpRequest request,
                              final EasyHttpResponse<?> response) throws
            UnsafeRedirectionException, RedirectionCanNotBeHandledException {
        int responseStatus = response.getStatus();
        final Header locationHeader = response.getResponseHeaders()
                .stream()
                .filter(header -> header.getKey().equalsIgnoreCase("location"))
                .findFirst()
                .orElseThrow();
        final Method requestMethod = request.getMethod();

        final String resourceLocation = locationHeader.getValue();
        final boolean isLocationURLAbsolute = URI.create(resourceLocation).isAbsolute();

        try{
            URL locationURL = new URL(resourceLocation);

            if(locationURL.getProtocol().equals("http") &&
                    this.redirectSafety.equals(RedirectSafety.ALWAYS)){
                String msg = "Redirection blocked because switching to http from https occurred";
                GenericError error = new GenericError(responseStatus,response.getResponseHeaders(),msg);
                Event.operation.publish(Channels.ERROR_CHANNEL, error);
                throw new UnsafeRedirectionException(error);
            }

            if(!isLocationURLAbsolute){
                locationURL = this.createLocationURL(request);

            }

            boolean isRedirectdable = this.checkIfRedirectCanBeHandled(response, request) &&
                    this.redirectSafety.equals(RedirectSafety.ALWAYS);

            if(isRedirectdable){
                request.setUrl(locationURL);
            }else{
                throw new RedirectionCanNotBeHandledException(new GenericError(responseStatus,
                        response.getResponseHeaders(),"Unsuccessful attempting to handle redirect"));
            }
        }catch (MalformedURLException e){
            GenericError genericError = new GenericError(responseStatus, response.getResponseHeaders(), e.getMessage());
            Event.operation.publish(Channels.ERROR_CHANNEL, new ErrorCommunicate(genericError));
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
        final Method method = request.getMethod();

        final Header locationHeader = response.getResponseHeaders()
                .stream()
                .filter(header -> header.getKey().equalsIgnoreCase("location"))
                .findFirst()
                .orElse(null);

        return switch (statusCode){
            case HttpURLConnection.HTTP_MOVED_PERM, 307,
                 HttpURLConnection.HTTP_MOVED_TEMP -> isRedirectable(method) && locationHeader!=null;
            case HttpURLConnection.HTTP_SEE_OTHER -> isRedirectable(method) && method.equals(Method.GET);
            default -> false;
        };
    }

    public boolean checkIfProxyRequiredForRedirection(final EasyHttpResponse<?> response){
        return response.getStatus() == HttpURLConnection.HTTP_USE_PROXY;
    }

    private boolean isRedirectable(final Method method) {
        return this.redirectableMethods.contains(method);
    }

    private URL createLocationURL(final EasyHttpRequest request) throws MalformedURLException {
        URL resourceUrl = request.getUrl();

        final String host = resourceUrl.getHost();
        final String path = resourceUrl.getPath();
        final String protocol = resourceUrl.getProtocol();

        int port = resourceUrl.getPort();

        String locationStr = protocol.concat("://").concat(host);

        if(port != -1){
            locationStr+=locationStr.concat(":").concat(String.valueOf(port));
        }

        locationStr = locationStr.concat(path);

        return new URL(locationStr);
    }
}