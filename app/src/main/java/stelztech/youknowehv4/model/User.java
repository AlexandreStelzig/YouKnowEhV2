package stelztech.youknowehv4.model;

/**
 * Created by alex on 2017-05-11.
 */

public class User {

    private String userId;
    private String dateCreated;
    private String activeProfileId;
    private int defaultSortingPosition;


    public User(String userId, String dateCreated, String activeProfileId, int defaultSortingPosition) {
        this.userId = userId;
        this.dateCreated = dateCreated;
        this.activeProfileId = activeProfileId;
        this.defaultSortingPosition = defaultSortingPosition;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getActiveProfileId() {
        return activeProfileId;
    }

    public void setActiveProfileId(String activeProfileId) {
        this.activeProfileId = activeProfileId;
    }

    public int getDefaultSortingPosition() {
        return defaultSortingPosition;
    }

    public void setDefaultSortingPosition(int defaultSortingPosition) {
        this.defaultSortingPosition = defaultSortingPosition;
    }
}
