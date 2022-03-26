package requests.multirpart.simplerequest.jsonsender.bodysenders;

import Parts.FilePart;
import Parts.PartType;
import Parts.TextPart;
import requests.easyrequest.MultipartBody;
import requests.multirpart.simplerequest.jsonsender.BodyProvider;

import java.io.*;
import java.net.HttpURLConnection;

public class MultipartBodyProvider extends BodyProvider<MultipartBody> {

    public MultipartBodyProvider(MultipartBody request) {
        super(request);
    }

    @Override
    public void prepareAndCopyToStream() throws IOException {
        MultipartBody multipartBody = super.getRequest();
        PartType partType = multipartBody.getPartType();
        switch (partType) {
            case TEXT -> copyTextParts(multipartBody);
            case FILE -> copyFileParts(multipartBody);
            default -> throw new IllegalStateException("Invalid part type");
        };
    }

    private void copyTextParts(MultipartBody multipartBody) throws UnsupportedEncodingException {
        String boundary = multipartBody.getBoundary();
        String lineSeparator = multipartBody.getBodySeparator();
        String encoding = multipartBody.getEncoding();
        try(DataOutputStream printWriter = new DataOutputStream(super.getOutputStream());){
            multipartBody.getParts().forEach(part -> {
                TextPart textPart = (TextPart) part;
                try{
                    printWriter.writeBytes("--"+boundary);
                    printWriter.writeBytes(lineSeparator);
                    printWriter.writeBytes("Content-Disposition: form-data; name=\"");
                    printWriter.writeBytes(textPart.getPartName());
                    printWriter.writeBytes("\"");
                    printWriter.writeBytes(lineSeparator);
                    printWriter.writeBytes("Content-Type: text/plain; charset=".concat(encoding));
                    printWriter.writeBytes(lineSeparator);
                    printWriter.writeBytes(lineSeparator);
                    printWriter.writeBytes(textPart.getContent());
                    printWriter.writeBytes(lineSeparator);
                }catch (IOException e){

                }


            });
            printWriter.writeBytes("--"+boundary+"--");
            printWriter.writeBytes(lineSeparator);
        }catch (IOException ignored){

        }

    }
    private void copyFileParts(MultipartBody multipartBody) throws IOException {
        String boundary = multipartBody.getBoundary();
        String lineSeparator = multipartBody.getBodySeparator();

        DataOutputStream printWriter = new DataOutputStream(super.getOutputStream());

        printWriter.writeBytes("--"+boundary+lineSeparator);

        multipartBody.getParts().forEach(part -> {
            FilePart filePart = (FilePart) part;
            try {
                printWriter.writeBytes("Content-Disposition: form-data; name=\"");
                    printWriter.writeBytes(filePart.getPartName());
                    printWriter.writeBytes("\" filename=\"");
                    printWriter.writeBytes(filePart.getFile().getName());
                    printWriter.writeBytes("\"");
                    printWriter.writeBytes(lineSeparator);
                    printWriter.writeBytes("Content-Type: ");
                    printWriter.writeBytes(HttpURLConnection.guessContentTypeFromName(filePart.getFile().getName()));
                    printWriter.writeBytes(lineSeparator);
                    printWriter.writeBytes(lineSeparator);
                File f = ((FilePart) part).getFile();

                InputStream reader = new FileInputStream(f);
                printWriter.write(reader.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                printWriter.writeBytes(lineSeparator+"--" + boundary + "--" + lineSeparator);
            }catch (IOException i){

            }

        });
    }

}
