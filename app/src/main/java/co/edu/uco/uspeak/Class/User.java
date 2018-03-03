package co.edu.uco.uspeak.Class;

/**
 * Created by Isabel on 16/01/2018.
 */

public class User {
    private int points;
    private String name;
    private String email;
    private String profilePicture;
    private String interest;
    private String uid;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }
}
