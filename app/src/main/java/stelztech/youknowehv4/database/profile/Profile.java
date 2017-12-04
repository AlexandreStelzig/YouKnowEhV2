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
    private String questionLabel;
    private String answerLabel;
    private int activeQuizId;
    private ThemeManager.THEME_COLORS profileColor;
    private String lastTimeOpened;
    private int profileImage;

    public Profile(int profileId, String profileName, String dateCreated, String questionLabel, String answerLabel, int activeQuizId, ThemeManager.THEME_COLORS profileColor, String lastTimeOpened, int profileImage) {
        this.profileId = profileId;
        this.profileName = profileName;
        this.dateCreated = dateCreated;
        this.questionLabel = questionLabel;
        this.answerLabel = answerLabel;
        this.activeQuizId = activeQuizId;
        this.profileColor = profileColor;
        this.lastTimeOpened = lastTimeOpened;
        this.profileImage = profileImage;
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

    public String getQuestionLabel() {
        return questionLabel;
    }

    public void setQuestionLabel(String questionLabel) {
        this.questionLabel = questionLabel;
    }

    public String getAnswerLabel() {
        return answerLabel;
    }

    public void setAnswerLabel(String answerLabel) {
        this.answerLabel = answerLabel;
    }

    public int getActiveQuizId() {
        return activeQuizId;
    }

    public void setActiveQuizId(int activeQuizId) {
        this.activeQuizId = activeQuizId;
    }
}
