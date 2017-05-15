package stelztech.youknowehv4.model;

/**
 * Created by alex on 2017-05-11.
 */

public class User {

    private String userId;
    private String dateCreated;
    private String activeProfileId;
    private int defaultSortingPosition;
    private boolean allowProfileDeletion;
    private boolean displayNbDecksAllCards;
    private boolean displayNbDecksSpecificCards;
    private boolean allowPracticeAll;
    private boolean allowOnQueryChanged;

    public User(String userId, String dateCreated, String activeProfileId, int defaultSortingPosition, boolean allowProfileDeletion, boolean displayNbDecksAllCards, boolean displayNbDecksSpecificCards, boolean allowPracticeAll, boolean allowOnQueryChanged) {
        this.userId = userId;
        this.dateCreated = dateCreated;
        this.activeProfileId = activeProfileId;
        this.defaultSortingPosition = defaultSortingPosition;
        this.allowProfileDeletion = allowProfileDeletion;
        this.displayNbDecksAllCards = displayNbDecksAllCards;
        this.displayNbDecksSpecificCards = displayNbDecksSpecificCards;
        this.allowPracticeAll = allowPracticeAll;
        this.allowOnQueryChanged = allowOnQueryChanged;
    }

    public boolean isAllowOnQueryChanged() {
        return allowOnQueryChanged;
    }

    public void setAllowOnQueryChanged(boolean allowOnQueryChanged) {
        this.allowOnQueryChanged = allowOnQueryChanged;
    }

    public boolean isAllowPracticeAll() {
        return allowPracticeAll;
    }

    public void setAllowPracticeAll(boolean allowPracticeAll) {
        this.allowPracticeAll = allowPracticeAll;
    }

    public boolean isAllowProfileDeletion() {
        return allowProfileDeletion;
    }

    public void setAllowProfileDeletion(boolean allowProfileDeletion) {
        this.allowProfileDeletion = allowProfileDeletion;
    }

    public boolean isDisplayNbDecksAllCards() {
        return displayNbDecksAllCards;
    }

    public void setDisplayNbDecksAllCards(boolean displayNbDecksAllCards) {
        this.displayNbDecksAllCards = displayNbDecksAllCards;
    }

    public boolean isDisplayNbDecksSpecificCards() {
        return displayNbDecksSpecificCards;
    }

    public void setDisplayNbDecksSpecificCards(boolean displayNbDecksSpecificCards) {
        this.displayNbDecksSpecificCards = displayNbDecksSpecificCards;
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
