package intercepting.patternintercepting;

import java.net.URL;

@FunctionalInterface
public interface Checker {
    boolean checkRequestUrl(URL url);
}
