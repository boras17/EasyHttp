package jsoncraetortests;

import jsonoperations.serialization.SerializedName;

import java.util.List;

public class UserWithSerializedNameAnnotation {
    @SerializedName(name = "user_id")
    private int id;
    @SerializedName(name = "name")
    private String username;
    @SerializedName(name="user_skills")
    private List<String> userSkills;
    @SerializedName(name="is_user_alive")
    private boolean alive;

    public UserWithSerializedNameAnnotation(int id, String username, List<String> userSkills, boolean alive) {
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
