package parts;

public class TextPart implements Part {
    private String content;
    private String partName;

    public TextPart(){

    }

    public TextPart(String content, String partName) {
        this.content = content;
        this.partName = partName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
}
