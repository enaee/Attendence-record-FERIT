package hr.ferit.zavrsni.Models;

public class User {
    private String userID;
    private String name;
    private Boolean isNewUser;

    public User() {
    }

    public Boolean getNewUser() {
        return isNewUser;
    }

    public void setNewUser(Boolean newUser) {
        isNewUser = newUser;
    }

    public User(String userID, String name, Boolean isNewUser) {
        this.userID = userID;
        this.name = name;
        this.isNewUser = isNewUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
