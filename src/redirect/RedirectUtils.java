package redirect;

import HttpEnums.Method;

import java.util.*;

public class RedirectUtils {
    public static Map<Integer, Set<Method>> getSaveMethodsForRedirects() {
        Map<Integer, Set<Method>> methods = new HashMap<>();
        methods.put(301, Collections.singleton(Method.GET));
        return methods;
    }
    public static Set<Method> getRedirectableMethods() {
        Set<Method> methods = new HashSet<>();
        methods.add(Method.POST);
        methods.add(Method.GET);
        return methods;
    }
    public static Map<Integer, List<Method>> saveMethodsForRedirect() {
        Map<Integer, List<Method>> map = new HashMap<>();

        return map;
    }
}
