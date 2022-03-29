package publishsubscribe.errorsubscriberimpl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SaveSubscriber extends Subscriber{

    private Path path;

    public SaveSubscriber(Path path){
        super();
    }

    @Override
    public void onMessage(Message message) {
        try{
            OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.APPEND);
            OutputStreamWriter outWriter = new OutputStreamWriter(outputStream);
            outWriter.append(message.getMessage());
            outWriter.append("\n");
            outWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
