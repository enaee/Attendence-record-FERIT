package hr.ferit.zavrsni.Models;

public class User {
    private String userID;
    private String name;


    public User() {
    }


    public User(String userID, String name) {
        this.userID = userID;
        this.name = name;
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
