package intercepting.patternintercepting;

import intercepting.Interceptor;
import Utils.simplerequest.EasyHttpRequest;

import java.net.URL;

public abstract class PatternRequestInterceptor implements Interceptor<EasyHttpRequest> {
    private final Checker checker;

    public PatternRequestInterceptor(Checker checker){
        this.checker = checker;
    }

    public boolean shouldIntercept(URL url){
        return this.checker.checkRequestUrl(url);
    }
}
