package stelztech.youknowehv4.database.quiz;

import java.util.Date;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Quiz {

    public enum MODE {
        READING,
        WRITING
    }


    private long quizId;
    private Date dateCreated;
    private Date dateFinished;
    private MODE mode;
    private boolean reverse;
    private boolean repeat;
    private boolean reviewOnly;
    private long profileId;

    public Quiz(long quizId, Date dateCreated, Date dateFinished, MODE mode, boolean reverse, boolean repeat, boolean reviewOnly, long profileId) {
        this.quizId = quizId;
        this.dateCreated = dateCreated;
        this.dateFinished = dateFinished;
        this.mode = mode;
        this.reverse = reverse;
        this.repeat = repeat;
        this.reviewOnly = reviewOnly;
        this.profileId = profileId;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public boolean isReviewOnly() {
        return reviewOnly;
    }

    public void setReviewOnly(boolean reviewOnly) {
        this.reviewOnly = reviewOnly;
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }
}
