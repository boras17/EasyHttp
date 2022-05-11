package declarativeclient;

import httpenums.HttpMethod;

public class MethodAndPath{
    private HttpMethod method;
    private String path;

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
