package stelztech.youknowehv4.database.profile;

import java.util.Date;

/**
 * Created by alex on 2017-05-06.
 */

public class Profile {


    private int profileId;
    private String profileName;
    private Date dateCreated;
    private String questionLabel;
    private String answerLabel;
    private String activeQuizId;


    public Profile(int profileId, String profileName, Date dateCreated, String questionLabel, String answerLabel, String activeQuizId) {
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
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

    public String getActiveQuizId() {
        return activeQuizId;
    }

    public void setActiveQuizId(String activeQuizId) {
        this.activeQuizId = activeQuizId;
    }
}
