package stelztech.youknowehv4.database.user;

import java.util.Date;

/**
 * Created by alex on 2017-05-11.
 */

public class User {

    private long userId;
    private Date dateCreated;
    private long activeProfileId;
    private int defaultSortingPosition;
    private boolean allowProfileDeletion;
    private boolean displayNbDecksAllCards;
    private boolean displayNbDecksSpecificCards;
    private boolean allowPracticeAll;
    private boolean allowOnQueryChanged;
    private int quickToggleHours;

    public User(long userId, Date dateCreated, long activeProfileId, int defaultSortingPosition, boolean allowProfileDeletion, boolean displayNbDecksAllCards, boolean displayNbDecksSpecificCards, boolean allowPracticeAll, boolean allowOnQueryChanged, int quickToggleHours) {
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getActiveProfileId() {
        return activeProfileId;
    }

    public void setActiveProfileId(long activeProfileId) {
        this.activeProfileId = activeProfileId;
    }

    public int getDefaultSortingPosition() {
        return defaultSortingPosition;
    }

    public void setDefaultSortingPosition(int defaultSortingPosition) {
        this.defaultSortingPosition = defaultSortingPosition;
    }
}
