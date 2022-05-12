package declarativeclient;

import client.EasyHttpClient;
import requests.bodyproviders.JsonBodyProvider;
import requests.bodyproviders.MultipartBodyProvider;
import declarativeclient.declarativeannotations.httprequests.*;
import declarativeclient.declarativeclienterrors.MethodAndPathAreRequired;
import headers.HttpHeader;
import requests.EasyHttpRequest;
import requests.bodyproviders.BodyProvider;
import declarativeclient.declarativeannotations.*;
import declarativeclient.declarativeclienterrors.CouldNotGuessBodyHandlerException;
import httpenums.HttpMethod;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.bodyhandlers.StreamBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyrequest.MultipartBody;
import requests.EasyHttpResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;


public class DeclaredClientProxy implements InvocationHandler {

    private EasyHttpClient easyHttp;

    public DeclaredClientProxy(EasyHttpClient easyHttp){
        this.easyHttp = easyHttp;
    }

    public DeclaredClientProxy(){}

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        EasyHttpRequest easyHttpRequest = this.calculateRequest(method, args);

        return this.performRequest(easyHttpRequest, method.getReturnType());
    }

    private Object performRequest(EasyHttpRequest request, Class<?> method_return_type) throws IOException, RedirectionUnhandled, IllegalAccessException {
        AbstractBodyHandler<?> bodyHandler = this.guessBodyHandler(method_return_type);
        EasyHttpResponse<?> response = this.easyHttp.send(request, bodyHandler);

        if(response!=null){
            return response.getBody();
        }
        return null;
    }

    public EasyHttpRequest calculateRequest(Method method, Object[] args) throws IOException{
        EasyHttpRequest.EasyHttpRequestBuilder request_builder = new EasyHttpRequest.EasyHttpRequestBuilder();

        boolean headers_annotation_present       = method.isAnnotationPresent(Headers.class);
        boolean single_annotation_present        = method.isAnnotationPresent(Header.class);
        boolean path_method_annotation_present   = method.isAnnotationPresent(Get.class)    ||
                                                   method.isAnnotationPresent(Post.class)   ||
                                                   method.isAnnotationPresent(Put.class)    ||
                                                   method.isAnnotationPresent(Delete.class) ||
                                                   method.isAnnotationPresent(Patch.class);
        boolean body_provider_annotation_present = this.checkIfAnnotationPresentInMethodParameters(RequestBodyProvider.class, method);
        boolean multipart_annotation_present     = this.checkIfAnnotationPresentInMethodParameters(Multipart.class, method);
        boolean request_body_annotation_present  = this.checkIfAnnotationPresentInMethodParameters(RequestJsonBody.class, method);
        boolean request_proxy_annotation_present = method.isAnnotationPresent(RequestProxy.class);

        boolean request_params_are_present  = this.checkIfAnnotationPresentInMethodParameters(RequestParam.class, method);
        boolean path_annotation_replacement = this.checkIfAnnotationPresentInMethodParameters(PathVariable.class, method);

        if(path_method_annotation_present){

            Map<RequestParam, Object> request_params = null;
            Map<PathVariable, Object> pathVariables = null;

            if(request_params_are_present) {
                request_params = this.createAnnotationParametersMap(method, args, RequestParam.class);
            }
            if(path_annotation_replacement) {
                pathVariables = this.createAnnotationParametersMap(method, args, PathVariable.class);
            }

            MethodAndPath methodAndPath = this.getHttpMethodAndPath(method, pathVariables, request_params);
            request_builder.setMethod(methodAndPath.getMethod());

            request_builder.setUri(new URL(methodAndPath.getPath()));
        }else{
            throw new MethodAndPathAreRequired("You have to specify Get Post Delete etc... annotation");
        }

        if(request_proxy_annotation_present) {
            Proxy proxy = this.getProxyFromAnnotation(method);
            request_builder.setProxy(proxy);
        }
        if(request_body_annotation_present) {
            Object request_body_data = this.getParameterForAnnotation(RequestJsonBody.class, method,args);
            request_builder.setBodyProvider(new JsonBodyProvider(request_body_data));
        }
        else if(multipart_annotation_present){
            MultipartBody multipartBody = this.getParameterForAnnotation(Multipart.class, method, args);
            request_builder.setBodyProvider(new MultipartBodyProvider(multipartBody));
        }
        if(headers_annotation_present){
            Headers headers = method.getAnnotation(Headers.class);
            List<HttpHeader> extracted_http_headers = this.calculateHeaders(headers);
            request_builder.addHeaders(extracted_http_headers);
        }else if(single_annotation_present) {
            Header header = method.getAnnotation(Header.class);
            request_builder.addHeader(this.calculateHeader(header));
        }

        if(body_provider_annotation_present) {
            BodyProvider<?> bodyProvider = this.getParameterForAnnotation(RequestBodyProvider.class, method, args);
            request_builder.setBodyProvider(bodyProvider);
        }

        return request_builder.build();
    }

    private Proxy getProxyFromAnnotation(Method method){
        RequestProxy requestProxy = method.getAnnotation(RequestProxy.class);
        ProxyHostAndPort proxyHostAndPort = requestProxy.proxyServer();
        Proxy.Type proxyType = requestProxy.type();
        String proxyHost = proxyHostAndPort.host();
        int proxyPort = proxyHostAndPort.port();
        return new Proxy(proxyType, new InetSocketAddress(proxyHost,proxyPort));
    }

    private List<HttpHeader> calculateHeaders(Headers headers) {
        Header[] _header = headers.headers();
        List<HttpHeader> httpHeaders = new ArrayList<>();

        for(Header header: _header) {
            httpHeaders.add(this.calculateHeader(header));
        }

        return httpHeaders;
    }
    private HttpHeader calculateHeader(Header header) {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.setKey(header.key());
        httpHeader.setValue(header.value());
        return httpHeader;
    }

    private String addPathVariables(String endpoint , Map<PathVariable, Object> replace_replacement){
        for(Map.Entry<PathVariable, Object> entry: replace_replacement.entrySet()) {
            endpoint = endpoint.replace("{".concat(entry.getKey().value()).concat("}"), String.valueOf(entry.getValue()));
        }
        return endpoint;
    }

    private boolean checkIfAnnotationPresentInMethodParameters(Class<? extends Annotation> annotationType, Method method) {
        Annotation[][] annotations = method.getParameterAnnotations();

        for (Annotation[] annotation : annotations) {
            for (Annotation value : annotation) {
                if (value.annotationType() == annotationType) {
                    return true;
                }
            }
        }

        return false;
    }

    private <T extends Annotation> Map<T, Object> createAnnotationParametersMap(Method method, Object[] args, Class<T> tClass) {
        Map<T, Object> result = new LinkedHashMap<>();

        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        for (int i = 0; i < paramAnnotations.length; i++) {
            for (int j = 0; j < paramAnnotations[i].length; j++) {
                Annotation annotation = paramAnnotations[i][j];
                if(annotation.annotationType() == tClass){
                    result.put((T)annotation, args[i]);
                }
            }
        }

        return result;
    }

    public String addRequestParameters(String url, Map<RequestParam,Object> param_value){
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append("?");

        List<Map.Entry<RequestParam, Object>> entries = new ArrayList<>(param_value.entrySet());
        int entries_size = entries.size();

        for(int i = 0; i < entries_size; ++i) {
            Map.Entry<RequestParam, Object> entry = entries.get(i);
            urlBuilder.append(entry.getKey().value())
                    .append("=")
                    .append(entry.getValue())
                    .append(i != entries_size - 1 ? "&":"");
        }
        return urlBuilder.toString();
    }

    public <T> T getParameterForAnnotation(Class<? extends Annotation> wanted_annotation, Method method, Object[] args) {
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[j].length; j++) {
                Annotation annotation = annotations[i][j];
                if(annotation.annotationType() == wanted_annotation){
                    return (T)args[i];
                }
            }
        }
        return null;
    }

    private AbstractBodyHandler<?> guessBodyHandler(Class<?> methodReturnedType) {
        boolean response_json = methodReturnedType.equals(String.class);
        boolean response_stream = methodReturnedType.equals(Stream.class);
        boolean response_void = methodReturnedType.equals(Void.class) || methodReturnedType.equals(void.class);
        if(response_json){
            return new StringBodyHandler();
        }else if(response_stream) {
            return new StreamBodyHandler();
        }else if(response_void) {
            return new EmptyBodyHandler();
        }else{
            throw new CouldNotGuessBodyHandlerException("could not guess body handler for method return type: " + methodReturnedType);
        }
    }

    private MethodAndPath getHttpMethodAndPath(Method method, Map<PathVariable,Object> pathVariables, Map<RequestParam,Object> requestParameters){
        boolean get    = method.isAnnotationPresent(Get.class);
        boolean post   = method.isAnnotationPresent(Post.class);
        boolean patch  = method.isAnnotationPresent(Patch.class);
        boolean put    = method.isAnnotationPresent(Put.class);
        boolean delete = method.isAnnotationPresent(Delete.class);

        MethodAndPath methodAndPath = new MethodAndPath();

        if(get){
            Get getAnnotation = method.getAnnotation(Get.class);
            methodAndPath.setPath(getAnnotation.value());
            methodAndPath.setMethod(HttpMethod.GET);
        }else if(post){
            Post postAnnotation = method.getAnnotation(Post.class);
            methodAndPath.setPath(postAnnotation.value());
            methodAndPath.setMethod(HttpMethod.POST);
        }else if(delete) {
            Delete deleteAnnotation = method.getAnnotation(Delete.class);
            methodAndPath.setPath(deleteAnnotation.value());
            methodAndPath.setMethod(HttpMethod.DELETE);
        }else if(patch) {
            Patch patchAnnotation = method.getAnnotation(Patch.class);
            methodAndPath.setMethod(HttpMethod.PATCH);
            methodAndPath.setPath(patchAnnotation.value());
        }else if(put){
            Put putAnnotation = method.getAnnotation(Put.class);
            methodAndPath.setPath(putAnnotation.value());
            methodAndPath.setMethod(HttpMethod.PUT);
        }

        int pathVariablesSize       = pathVariables     == null ? 0 : pathVariables.size(),
            requestParametersSize   = requestParameters == null ? 0 : requestParameters.size();

        if(pathVariablesSize > 0) {
            methodAndPath.setPath(this.addPathVariables(methodAndPath.getPath(), pathVariables));
        }
        if(requestParametersSize > 0) {
            methodAndPath.setPath(this.addRequestParameters(methodAndPath.getPath(), requestParameters));
        }

        return methodAndPath;
    }
}
