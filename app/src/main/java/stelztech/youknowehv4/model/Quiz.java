package stelztech.youknowehv4.model;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Quiz {

    private enum MODE {
        READING,
        WRITING
    }

    private String quizId;
    private String dateCreated;
    private String dateFinished;
    private MODE mode;
    private boolean reverse;
    private boolean repeat;
    private boolean reviewOnly;

    public Quiz(String quizId, String dateCreated, String dateFinished, MODE mode, boolean reverse, boolean repeat, boolean reviewOnly) {
        this.quizId = quizId;
        this.dateCreated = dateCreated;
        this.dateFinished = dateFinished;
        this.mode = mode;
        this.reverse = reverse;
        this.repeat = repeat;
        this.reviewOnly = reviewOnly;
    }

    public boolean isReviewOnly() {
        return reviewOnly;
    }

    public void setReviewOnly(boolean reviewOnly) {
        this.reviewOnly = reviewOnly;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(String dateFinished) {
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
