package jsoncraetortests;

import java.util.List;

public class UserWithNestListOfObjects {
    private int age;
    List<Username> usernameList;

    public UserWithNestListOfObjects(int age, List<Username> usernameList) {
        this.age = age;
        this.usernameList = usernameList;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<Username> getUsernameList() {
        return usernameList;
    }

    public void setUsernameList(List<Username> usernameList) {
        this.usernameList = usernameList;
    }
}
