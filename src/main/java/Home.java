import Utils.simplerequest.EasyHttpRequest;
import intercepting.Interceptor;
import intercepting.patternintercepting.Checker;
import intercepting.patternintercepting.PathPatternChecker;
import intercepting.patternintercepting.PatternRequestInterceptor;

public class Home {

    static class PatternInterceptor extends PatternRequestInterceptor{

        public PatternInterceptor(Checker checker) {
            super(checker);
        }

        @Override
        public void handle(EasyHttpRequest easyHttpRequest) {
            boolean matcher = this.shouldIntercept(easyHttpRequest.getUrl());
        }
    }

    public static void main(String[] args) {
        Interceptor<EasyHttpRequest> requestInterceptor = new PatternInterceptor(new PathPatternChecker("some pattern"));
    }
}
