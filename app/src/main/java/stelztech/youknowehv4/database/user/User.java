package stelztech.youknowehv4.database.user;

/**
 * Created by alex on 2017-05-11.
 */

public class User {

    private int userId;
    private String dateCreated;
    private int activeProfileId;
    private int defaultSortingPosition;
    private boolean allowProfileDeletion;
    private boolean displayNbDecksAllCards;
    private boolean displayNbDecksSpecificCards;
    private boolean allowPracticeAll;
    private boolean allowOnQueryChanged;
    private int quickToggleHours;

    public User(int userId, String dateCreated, int activeProfileId, int defaultSortingPosition, boolean allowProfileDeletion, boolean displayNbDecksAllCards, boolean displayNbDecksSpecificCards, boolean allowPracticeAll, boolean allowOnQueryChanged, int quickToggleHours) {
        this.userId = userId;
        this.dateCreated = dateCreated;
        this.activeProfileId = activeProfileId;
        this.defaultSortingPosition = defaultSortingPosition;
        this.allowProfileDeletion = allowProfileDeletion;
        this.displayNbDecksAllCards = displayNbDecksAllCards;
        this.displayNbDecksSpecificCards = displayNbDecksSpecificCards;
        this.allowPracticeAll = allowPracticeAll;
        this.allowOnQueryChanged = allowOnQueryChanged;
        this.quickToggleHours = quickToggleHours;
    }


    public int getQuickToggleHours() {
        return quickToggleHours;
    }

    public void setQuickToggleHours(int quickToggleHours) {
        this.quickToggleHours = quickToggleHours;
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

    public int getDefaultSortingPosition() {
        return defaultSortingPosition;
    }

    public void setDefaultSortingPosition(int defaultSortingPosition) {
        this.defaultSortingPosition = defaultSortingPosition;
    }
}
