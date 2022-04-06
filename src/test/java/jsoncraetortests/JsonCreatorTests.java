package jsoncraetortests;

import jsonoperations.JsonCreator;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JsonCreatorTests {

    JsonCreator jsonCreator;

    @Before
    public void initJsonCreator() {
        jsonCreator = new JsonCreator();
    }

    @Test
    public void givenSimpleUserShouldReturnJsonSerialized() throws IllegalAccessException, IOException {
        User user = new User(1, "Adam", Arrays.asList("Java", "Python"),true);
        jsonCreator.generateJson(user);
        String serializedUser = jsonCreator.getJson();
        String expectedJson = "{\"id\":1,\"username\":\"Adam\",\"userSkills\":[\"Java\",\"Python\"],\"alive\":true}";
        Assertions.assertEquals(serializedUser, expectedJson);
    }
    @Test
    public void givenArrayListToJsonCreatorShouldReturnJsonList() throws IllegalAccessException {
        List<String> list = Arrays.asList("one", "two", "three");
        jsonCreator.generateJson(list);
        String expected = "[\"one\",\"two\",\"three\"]";
        Assertions.assertEquals(expected, jsonCreator.getJson());
    }

    @Test
    public void givenObjectWithCollectionAndOneFieldShouldReturnCorrectJson() throws IllegalAccessException {
        User user = new User(123,"Ben", List.of("One", "Two"), true);
        this.jsonCreator.generateJson(user);
        String expected = "{\"id\":123,\"username\":\"Ben\",\"userSkills\":[\"One\",\"Two\"],\"alive\":true}";
        String generated = this.jsonCreator.getJson();
        Assertions.assertEquals(expected, generated);
    }

    @Test
    public void givenObjectWithNestedObjectShouldReturnCorrectJsonWithNestedObjects() throws IllegalAccessException {
        UserWithNestedObject userWithNestedObject = new UserWithNestedObject(1, new Username("Ben", "Gates"));
        jsonCreator.generateJson(userWithNestedObject);
        String generatedJson = jsonCreator.getJson();
        String expectedJson = "{\"id\":1,\"username\":{\"firstName\":\"Ben\",\"lastName\":\"Gates\"}}";
        Assertions.assertEquals(expectedJson, generatedJson);
    }

    @Test
    public void givenNullFieldShouldCorrectlySerializeNull() throws IllegalAccessException {
        UserWithNestedObject user = new UserWithNestedObject(1, null);
        jsonCreator.generateJson(user);
        String generatedJson = jsonCreator.getJson();
        String expectedJson = "{\"id\":1,\"username\":null}";
        Assertions.assertEquals(expectedJson, generatedJson);
    }

    @Test
    public void givenUserWithFieldWithNestedObjectShouldReturnCorrectJson() throws IllegalAccessException {
        List<Username> usernames = List.of(new Username("Ben", "Gates"), new Username("Adam", "Nowak"));
        UserWithNestListOfObjects user = new UserWithNestListOfObjects(22, usernames);
        jsonCreator.generateJson(user);
        String generatedJson = jsonCreator.getJson();
        String shouldReturn = "{\"age\":22,\"usernameList\":[{\"firstName\":\"Ben\",\"lastName\":\"Gates\"},{\"firstName\":\"Adam\",\"lastName\":\"Nowak\"}]}";
        Assertions.assertEquals(shouldReturn, generatedJson);
    }
}

