package requests.easyrequest;

import parts.Part;
import parts.PartType;

import java.util.ArrayList;
import java.util.List;

public class MultipartBody {
    private final String bodySeparator = "\r\n";
    private final String boundary = "myboundary";
    private PartType partType;

    private List<Part> parts = new ArrayList<>();
    private String encoding;

    public static class MultiPartBodyBuilder{

        private List<Part> parts = new ArrayList<>();
        private String encoding;
        private PartType partType;

        public MultiPartBodyBuilder addPart(Part part){
            parts.add(part);
            return this;
        }

        public MultiPartBodyBuilder setPartType(PartType partType){
            this.partType = partType;
            return this;
        }

        public MultiPartBodyBuilder encoding(String encoding){
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
