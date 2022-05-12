package publishsubscribe.errorsubscriberimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public abstract class Subscriber<T> {

     private Properties properties;

     public Subscriber(Properties properties){
          this.properties = properties;
     }

     void onRedirectErrorCommunicate(T redirectError){}

     void onClientErrorCommunicate(T clientError){}

     void onServerErrorCommunicate(T serverError){}

     void onAppError(T applicationError){}

     void onNotification(T notification){}

     protected void writeError(String communicate, String errorFile, Properties properties, StandardOpenOption fileOpenOption){
          Path path = Paths.get(errorFile);
          try{
               Files.writeString(path, communicate.concat(System.lineSeparator()), fileOpenOption);
          } catch (IOException fileNotFoundException) {
               fileNotFoundException.printStackTrace();
          }
     }

     public Properties getProperties() {
          return properties;
     }
}
