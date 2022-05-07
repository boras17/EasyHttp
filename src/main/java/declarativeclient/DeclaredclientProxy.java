package declarativeclient;

import Headers.HttpHeader;
import Utils.ReflectionUtils;
import Utils.simplerequest.EasyHttpRequest;
import Utils.simplerequest.jsonsender.BodyProvider;
import client.EasyHttp;
import declarativeclient.declarativeannotations.*;
import declarativeclient.declarativeclienterrors.CouldNotGuessBodyHandlerException;
import declarativeclient.declarativeclienterrors.PathAnnotationIsRequiredException;
import declarativeclient.declarativeclienterrors.RequestMethodIsRequiredException;
import redirect.redirectexception.RedirectionUnhandled;
import requests.bodyhandlers.AbstractBodyHandler;
import requests.bodyhandlers.EmptyBodyHandler;
import requests.bodyhandlers.StreamBodyHandler;
import requests.bodyhandlers.StringBodyHandler;
import requests.easyresponse.EasyHttpResponse;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;


public class DeclaredclientProxy implements InvocationHandler {

    private final EasyHttp easyHttp;
    private Consumer<EasyHttpResponse<?>> responseConsumer;

    public DeclaredclientProxy(EasyHttp easyHttp, Consumer<EasyHttpResponse<?>> responseConsumer){
        this.easyHttp = easyHttp;
        this.responseConsumer = responseConsumer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return this.performRequest(method, args);
    }

    public Object performRequest(Method method, Object[] args) throws IOException, RedirectionUnhandled, IllegalAccessException {
        EasyHttpRequest.EasyHttpRequestBuilder request_builder = new EasyHttpRequest.EasyHttpRequestBuilder();

        String endpoint_url = "";

        boolean headers_annotation_present       = method.isAnnotationPresent(Headers.class);
        boolean single_annotation_present        = method.isAnnotationPresent(Header.class);
        boolean request_params_are_present       = this.checkIfAnnotationPresentInMethodParameters(RequestParam.class, method);
        boolean path_annotation_present          = method.isAnnotationPresent(Path.class);
        boolean path_annotation_replacement      = this.checkIfAnnotationPresentInMethodParameters(PathVariable.class, method);
        boolean body_provider_annotation_present = this.checkIfAnnotationPresentInMethodParameters(RequestBodyProvider.class, method);
        boolean method_annotation_present        = method.isAnnotationPresent(RequestMethod.class);

        if(method_annotation_present) {
            RequestMethod requestMethod = method.getAnnotation(RequestMethod.class);
            request_builder.setMethod(requestMethod.method());
        }else{
            throw new RequestMethodIsRequiredException("Request method annotation for declared interface method is required");
        }

        if(headers_annotation_present){
            Headers headers = method.getAnnotation(Headers.class);
            List<HttpHeader> extracted_http_headers = this.calculateRequest(headers);
            request_builder.addHeaders(extracted_http_headers);
        }else if(single_annotation_present) {
            Header header = method.getAnnotation(Header.class);
            request_builder.addHeader(this.calculateRequest(header));
        }
        if(path_annotation_present){
            Path path = method.getAnnotation(Path.class);

            if(path_annotation_replacement) {
                Map<PathVariable,Object> path_replacements = this.createAnnotationParametersMap(method, args, PathVariable.class);
                endpoint_url = this.calculateEndpoint(path, path_replacements);
            }else{
                endpoint_url = this.calculateEndpoint(path);
            }
        }else{
            throw new PathAnnotationIsRequiredException("You have to specify endpoint url!");
        }

        if(request_params_are_present) {
            Map<RequestParam, Object> request_params = this.createAnnotationParametersMap(method, args, RequestParam.class);
            endpoint_url = this.addRequestParameters(request_params, endpoint_url);
        }

        if(body_provider_annotation_present) {
            BodyProvider<?> bodyProvider = this.getParameterForAnnotation(RequestBodyProvider.class, method, args);
            request_builder.setBodyProvider(bodyProvider);
        }

        EasyHttpRequest request = request_builder.build();
        request.setUrl(new URL(endpoint_url));

        Class<?> method_return_type = method.getReturnType();
        AbstractBodyHandler<?> bodyHandler = this.guessBodyHandler(method_return_type);
        EasyHttpResponse<?> response = this.easyHttp.send(request, bodyHandler);

        assert response != null;
        System.out.println(request.getUrl());
        return response.getBody();
    }

    private List<HttpHeader> calculateRequest(Headers headers) {
        Header[] _header = headers.headers();
        List<HttpHeader> httpHeaders = new ArrayList<>();

        for(Header header: _header) {
            httpHeaders.add(this.calculateRequest(header));
        }

        return httpHeaders;
    }
    private HttpHeader calculateRequest(Header header) {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.setKey(header.key());
        httpHeader.setValue(header.value());
        return httpHeader;
    }

    private String calculateEndpoint(Path path) {
        return path.value();
    }

    private String calculateEndpoint(Path path, Map<PathVariable, Object> replace_replacement){
        String endpoint = path.value();
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
        Map<T, Object> result = new HashMap<>();

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

    public String addRequestParameters(Map<RequestParam,Object> param_value, String url){
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

    private  AbstractBodyHandler<?> guessBodyHandler(Class<?> methodReturnedType) {
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
}
