package requests.easyrequest;

import Parts.Part;
import Parts.PartType;

import java.util.ArrayList;
import java.util.List;

public class MultipartBody extends EasyHttpRequest {
    private final String bodySeparator = "\r\n";
    private final String boundary = "myboundary";
    private PartType partType;

    private List<Part> parts = new ArrayList<>();
    private String encoding;

    public MultipartBody() {}

    public static class MultiPartRequestBuilder{

        private List<Part> parts = new ArrayList<>();
        private String encoding;
        private PartType partType;

        public MultiPartRequestBuilder addPart(Part part){
            parts.add(part);
            return this;
        }

        public MultiPartRequestBuilder setPartType(PartType partType){
            this.partType = partType;
            return this;
        }

        public MultiPartRequestBuilder encoding(String encoding){
            this.encoding = encoding;
            return this;
        }

        public MultipartBody build(){
            MultipartBody multipartBody = new MultipartBody();
            multipartBody.setEncoding(this.encoding);
            multipartBody.setParts(this.parts);
            multipartBody.setPartType(this.partType);
            return multipartBody;
        }

    }

    public PartType getPartType() {
        return partType;
    }

    public void setPartType(PartType partType) {
        this.partType = partType;
    }

    public String getBodySeparator() {
        return bodySeparator;
    }

    public String getBoundary() {
        return boundary;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
