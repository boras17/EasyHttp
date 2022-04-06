package jsoncraetortests;

public class UserWithNestedObject {
    private int id;
    private Username username;

    public UserWithNestedObject(int id, Username username){
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Username getUsername() {
        return username;
    }

    public void setUsername(Username username) {
        this.username = username;
    }
}
