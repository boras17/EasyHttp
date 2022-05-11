package redirect;

import headers.HttpHeader;
import httpenums.HttpMethod;
import redirect.redirectexception.RedirectWithoutLocationException;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Set;

public class RedirectionHandler extends AbstractRedirectionHandler{

    public RedirectionHandler(Set<HttpMethod> redirectableHttpMethods, RedirectSafety redirectSafety) {
        super(redirectableHttpMethods, redirectSafety);
    }

    public RedirectionHandler(Set<HttpMethod> redirectableHttpMethods){
        this(redirectableHttpMethods, RedirectSafety.ALWAYS);
    }

    @Override
    public void modifyRequest(EasyHttpRequest request, EasyHttpResponse<?> response) throws MalformedURLException,
            RedirectionCanNotBeHandledException,
            UnsafeRedirectionException {
        final HttpHeader locationHttpHeader = response.getResponseHeaders()
                .stream()
                .filter(header -> header.getKey().equalsIgnoreCase("location"))
                .findFirst()
                .orElseThrow(() -> new RedirectWithoutLocationException("Redirection occurred but there is no Location"));

        final String resourceLocation = locationHttpHeader.getValue();
        final boolean isLocationURLAbsolute = URI.create(resourceLocation).isAbsolute();

        URL locationURL = new URL(resourceLocation);

        if(locationURL.getProtocol().equals("http") &&
                super.getRedirectSafety().equals(RedirectSafety.ALWAYS)){
            String msg = "Redirection blocked because switching to http from https occurred";
            throw new UnsafeRedirectionException(msg);
        }

        if(!isLocationURLAbsolute){
            URL requestUrl = request.getUrl();
            locationURL = this.createLocationURL(requestUrl, resourceLocation);
        }

        boolean isRedirectable = this.checkIfRedirectCanBeHandled(response, request) &&
                super.getRedirectSafety().equals(RedirectSafety.ALWAYS);

        if(isRedirectable){
            request.setUrl(locationURL);
        }else{
            throw new RedirectionCanNotBeHandledException("response is not redirectable");
        }
    }
}
