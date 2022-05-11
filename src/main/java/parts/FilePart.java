package parts;


import java.io.File;

public class FilePart implements Part {
    private File file;
    private String partName;

    public FilePart(File file, String partName){
        this.file = file;
        this.partName = partName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }
}
