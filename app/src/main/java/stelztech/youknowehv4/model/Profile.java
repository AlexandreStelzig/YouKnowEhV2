package stelztech.youknowehv4.model;

/**
 * Created by alex on 2017-05-06.
 */

public class Profile {


    private String profileId;
    private String profileName;
    private String dateCreated;
    private String questionLabel;
    private String answerLabel;


    public Profile(String profileId, String profileName, String dateCreated, String questionLabel, String answerLabel) {
        this.profileId = profileId;
        this.profileName = profileName;
        this.dateCreated = dateCreated;
        this.questionLabel = questionLabel;
        this.answerLabel = answerLabel;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
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

}
