import HttpEnums.Method;
import declarativeclient.DeclarativeClientParser;
import declarativeclient.declarativeannotations.Path;
import declarativeclient.declarativeannotations.PathVariable;
import declarativeclient.declarativeannotations.RequestMethod;

import java.io.IOException;

public class Home {

    interface UserCrud{
        @RequestMethod(method = Method.GET)
        @Path("https://jsonplaceholder.typicode.com/todos/{id}")
        String getUserById(@PathVariable("id") int id);
    }

    public static void main(String[] args) throws IOException {
        UserCrud userCrud = new DeclarativeClientParser<>(UserCrud.class).getImplementation();

        String data = userCrud.getUserById(1);

    }
}
