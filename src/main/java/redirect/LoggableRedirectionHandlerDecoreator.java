package redirect;

import client.refractorredclient.clients.loggingmodel.ClientSubscribers;
import publishsubscribe.ChannelMessageType;
import publishsubscribe.Operation;
import publishsubscribe.communcates.notifications.GenericAppError;
import publishsubscribe.communcates.notifications.GenericHttpError;
import publishsubscribe.constants.Channels;
import redirect.redirectexception.RedirectionCanNotBeHandledException;
import redirect.redirectexception.UnsafeRedirectionException;
import requests.EasyHttpRequest;
import requests.EasyHttpResponse;

import java.net.MalformedURLException;

public class LoggableRedirectionHandlerDecoreator extends AbstractRedirectionHandler{
    private ClientSubscribers clientSubscribers;
    private Operation operation;
    private RedirectionHandler redirectionHandler;

    public LoggableRedirectionHandlerDecoreator(RedirectionHandler redirectionHandler){
        this.redirectionHandler = redirectionHandler;
    }

    public void setClientSubscribers(ClientSubscribers clientSubscribers) {
        this.clientSubscribers = clientSubscribers;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public void modifyRequest(EasyHttpRequest request, EasyHttpResponse<?> response)  {
        try{
            redirectionHandler.modifyRequest(request, response);
        }catch ( RedirectionCanNotBeHandledException | UnsafeRedirectionException e){
            boolean errorCanBeLogged=clientSubscribers.checkIfSubscriberRegistered(Channels.REDIRECT_ERROR_CHANNEL, ClientSubscribers.ChannelScope.HTTP_ERROR_CHANNELS);
            if(errorCanBeLogged){
                int responseStatus = response.getStatus();
                this.operation.publish(Channels.REDIRECT_ERROR_CHANNEL, new GenericHttpError(responseStatus, response.getResponseHeaders(),e.getMessage(), ChannelMessageType.SERVER));
            }
        }catch (MalformedURLException e){
            boolean errorChanBeHandled = clientSubscribers.checkIfSubscriberRegistered(Channels.APP_ERROR_CHANNEL, ClientSubscribers.ChannelScope.APPLICATION_ERRORS);
            if(errorChanBeHandled){
                this.operation.publish(Channels.APP_ERROR_CHANNEL, new GenericAppError(e, ChannelMessageType.APP));
            }
        }

    }
}
