package intercepting.patternintercepting;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathPatternChecker implements Checker{
    private final String pathPattern;

    public PathPatternChecker(String pathPattern){
        this.pathPattern = pathPattern;
    }

    @Override
    public boolean checkRequestUrl(URL url) {
        Pattern pattern = Pattern.compile(this.pathPattern);
        Matcher matcher = pattern.matcher(url.getPath());
        return matcher.find();
    }
}
