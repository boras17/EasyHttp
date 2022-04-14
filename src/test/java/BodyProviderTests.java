import Parts.FilePart;
import Parts.PartType;
import Parts.TextPart;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.powermock.api.mockito.PowerMockito;
import requests.easyrequest.MultipartBody;
import requests.multirpart.simplerequest.jsonsender.bodysenders.JsonBodyProvider;
import requests.multirpart.simplerequest.jsonsender.bodysenders.MultipartBodyProvider;
import requests.multirpart.simplerequest.jsonsender.bodysenders.TextBodyProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class BodyProviderTests {

    private class Animal{
        private String name;
        private Integer age;
        public Animal(String name, Integer age){this.name = name; this.age = age;}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Test
    public void givenJsonBody_to_jsonBodyProvide_ShouldCopyJsonIntoStream() throws IOException, IllegalAccessException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        JsonBodyProvider bodyProvider = new JsonBodyProvider(new Animal("Lion", 12));
        bodyProvider.setOutputStream(byteArrayOutputStream);
        bodyProvider.prepareAndCopyToStream();

        String expectedStreamContent = "{\"name\":\"Lion\",\"age\":12}";
        String calculatedBody = byteArrayOutputStream.toString();

        Assertions.assertEquals(expectedStreamContent, calculatedBody);
    }

    @Test
    public void givenPlainTextBodyIntoTextBodyProvider() throws IOException, IllegalAccessException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        TextBodyProvider bodyProvider = new TextBodyProvider("hello 123");
        bodyProvider.setOutputStream(byteArrayOutputStream);
        bodyProvider.prepareAndCopyToStream();

        String expectedStreamContent = "hello 123";
        String calculatedBody = byteArrayOutputStream.toString();

        Assertions.assertEquals(expectedStreamContent, calculatedBody);
    }

    @Test
    public void givenMultipartBodyProvider() throws IOException {
        MultipartBody multipartBody = new MultipartBody.MultiPartBodyBuilder()
                .addPart(new TextPart("someContent" ,"Hello world"))
                .setPartType(PartType.TEXT)
                .encoding(StandardCharsets.UTF_8.displayName())
                .build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        MultipartBodyProvider bodyProvider = new MultipartBodyProvider(multipartBody);
        bodyProvider.setOutputStream(out);
        bodyProvider.prepareAndCopyToStream();

        String expected = "--myboundary";
        String contentDisposition = "Content-Disposition: form-data; name=\"Hello world\"";
        String contentType = "Content-Type: text/plain; charset=UTF-8";
        String content = "someContent";
        String end = "--myboundary--";

        Set<String> shouldContainsThisValues = Set.of(expected, contentDisposition, contentType, content, end);

        String calculated = out.toString();

        org.assertj.core.api.Assertions.assertThat(calculated)
                .contains(shouldContainsThisValues);
    }

    @Test
    public void givenMoreThanOnePartIntoMultipartBodyProvider() throws IOException {
        MultipartBody multipartBody = new MultipartBody.MultiPartBodyBuilder()
                .addPart(new TextPart("someContent" ,"Hello world"))
                .addPart(new TextPart("secondContent" ,"second hello world"))
                .setPartType(PartType.TEXT)
                .encoding(StandardCharsets.UTF_8.displayName())
                .build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        MultipartBodyProvider bodyProvider = new MultipartBodyProvider(multipartBody);
        bodyProvider.setOutputStream(out);
        bodyProvider.prepareAndCopyToStream();

        String expected = "--myboundary";
        String partName = "Content-Disposition: form-data; name=\"Hello world\"";
        String contentType = "Content-Type: text/plain; charset=UTF-8";
        String content = "someContent";
        String end = "--myboundary--";
        String second_partname = "Content-Disposition: form-data; name=\"second hello world\"";
        String second_content = "secondContent";

        Set<String> shouldContainsThisValues = Set.of(expected, partName, contentType, content, end, second_partname, second_content);
        String calculated = out.toString();

        org.assertj.core.api.Assertions.assertThat(calculated)
                .contains(shouldContainsThisValues);
    }

    @Test
    public void givenOneFilePartIntoMultipartBodyProvider() throws IOException {
        File file = new File("C:\\Users\\miko7\\IdeaProjects\\EasyHttp\\src\\test\\java\\somefile.txt");

        MultipartBody multipartBody = new MultipartBody.MultiPartBodyBuilder()
                .addPart(new FilePart(file,"someFile"))
                .encoding(StandardCharsets.UTF_8.displayName())
                .setPartType(PartType.FILE)
                .build();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MultipartBodyProvider provider = new MultipartBodyProvider(multipartBody);
        provider.setOutputStream(outputStream);
        provider.prepareAndCopyToStream();


        // TODO: complete testing for multipart
    }
}
