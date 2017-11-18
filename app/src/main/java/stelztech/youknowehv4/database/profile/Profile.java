package stelztech.youknowehv4.database.profile;

/**
 * Created by alex on 2017-05-06.
 */

public class Profile {


    private int profileId;
    private String profileName;
    private String dateCreated;
    private String questionLabel;
    private String answerLabel;
    private int activeQuizId;


    public Profile(int profileId, String profileName, String dateCreated, String questionLabel, String answerLabel, int activeQuizId) {
        this.profileId = profileId;
        this.profileName = profileName;
        this.dateCreated = dateCreated;
        this.questionLabel = questionLabel;
        this.answerLabel = answerLabel;
        this.activeQuizId = activeQuizId;
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
