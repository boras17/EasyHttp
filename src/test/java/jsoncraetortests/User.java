package jsoncraetortests;

import java.util.List;

public class User {
    private Integer id;
    private String username;
    private List<String> userSkills;
    private Boolean alive;

    public User(int id, String username, List<String> userSkills, boolean alive) {
        this.id = id;
        this.username = username;
        this.userSkills = userSkills;
        this.alive = alive;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getUserSkills() {
        return userSkills;
    }

    public void setUserSkills(List<String> userSkills) {
        this.userSkills = userSkills;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
