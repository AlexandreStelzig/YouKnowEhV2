package stelztech.youknowehv4.database.user;

/**
 * Created by alex on 2017-05-11.
 */

public class User {

    private int userId;
    private String dateCreated;
    private int activeProfileId;

    public User(int userId, String dateCreated, int activeProfileId) {
        this.userId = userId;
        this.dateCreated = dateCreated;
        this.activeProfileId = activeProfileId;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getActiveProfileId() {
        return activeProfileId;
    }

    public void setActiveProfileId(int activeProfileId) {
        this.activeProfileId = activeProfileId;
    }
}
