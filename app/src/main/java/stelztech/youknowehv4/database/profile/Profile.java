package stelztech.youknowehv4.database.profile;

import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 2017-05-06.
 */

public class Profile {

    public static final int NO_PROFILES = -1;
    public static final int NO_QUIZ = -1;

    private int profileId;
    private String profileName;
    private String dateCreated;
    private String frontLabel;
    private String backLabel;
    private int activeQuizId;
    private ThemeManager.THEME_COLORS profileColor;
    private String lastTimeOpened;
    private int profileImage;
    private boolean displayNbDecksAllCards;
    private boolean displayNbDecksSpecificCards;
    private boolean allowOnQueryChanged;
    private int defaultSortingPosition;
    private int quickToggleHours;

    public Profile(int profileId, String profileName, String dateCreated, String frontLabel, String backLabel, int activeQuizId, ThemeManager.THEME_COLORS profileColor, String lastTimeOpened, int profileImage, boolean displayNbDecksAllCards, boolean displayNbDecksSpecificCards, boolean allowOnQueryChanged, int defaultSortingPosition, int quickToggleHours) {
        this.profileId = profileId;
        this.profileName = profileName;
        this.dateCreated = dateCreated;
        this.frontLabel = frontLabel;
        this.backLabel = backLabel;
        this.activeQuizId = activeQuizId;
        this.profileColor = profileColor;
        this.lastTimeOpened = lastTimeOpened;
        this.profileImage = profileImage;
        this.displayNbDecksAllCards = displayNbDecksAllCards;
        this.displayNbDecksSpecificCards = displayNbDecksSpecificCards;
        this.allowOnQueryChanged = allowOnQueryChanged;
        this.defaultSortingPosition = defaultSortingPosition;
        this.quickToggleHours = quickToggleHours;
    }

    public String getLastTimeOpened() {
        return lastTimeOpened;
    }

    public void setLastTimeOpened(String lastTimeOpened) {
        this.lastTimeOpened = lastTimeOpened;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public ThemeManager.THEME_COLORS getProfileColor() {
        return profileColor;
    }

    public void setProfileColor(ThemeManager.THEME_COLORS profileColor) {
        this.profileColor = profileColor;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getFrontLabel() {
        return frontLabel;
    }

    public void setFrontLabel(String frontLabel) {
        this.frontLabel = frontLabel;
    }

    public String getBackLabel() {
        return backLabel;
    }

    public void setBackLabel(String backLabel) {
        this.backLabel = backLabel;
    }

    public int getActiveQuizId() {
        return activeQuizId;
    }

    public void setActiveQuizId(int activeQuizId) {
        this.activeQuizId = activeQuizId;
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

    public boolean isAllowOnQueryChanged() {
        return allowOnQueryChanged;
    }

    public void setAllowOnQueryChanged(boolean allowOnQueryChanged) {
        this.allowOnQueryChanged = allowOnQueryChanged;
    }

    public int getDefaultSortingPosition() {
        return defaultSortingPosition;
    }

    public void setDefaultSortingPosition(int defaultSortingPosition) {
        this.defaultSortingPosition = defaultSortingPosition;
    }

    public int getQuickToggleHours() {
        return quickToggleHours;
    }

    public void setQuickToggleHours(int quickToggleHours) {
        this.quickToggleHours = quickToggleHours;
    }
}
