package stelztech.youknowehv4.database.quiz;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Quiz {

    public enum MODE {
        READING,
        WRITING,
        MULTIPLE_CHOICE
    }

    public static MODE getModeFromPosition(int position){
        if(position == 0){
            return Quiz.MODE.READING;
        }else if(position == 1){
            return Quiz.MODE.WRITING;
        }else{
            return Quiz.MODE.MULTIPLE_CHOICE;
        }
    }

    private int quizId;
    private String dateCreated;
    private String dateFinished;
    private MODE mode;
    private boolean reverse;
    private boolean reviewOnly;
    private int profileId;
    private int totalPassed;
    private int totalFailed;
    private int totalSkipped;

    public Quiz(int quizId, String dateCreated, String dateFinished, MODE mode, boolean reverse, boolean reviewOnly, int profileId, int totalPassed, int totalFailed, int totalSkipped) {
        this.quizId = quizId;
        this.dateCreated = dateCreated;
        this.dateFinished = dateFinished;
        this.mode = mode;
        this.reverse = reverse;
        this.reviewOnly = reviewOnly;
        this.profileId = profileId;
        this.totalPassed = totalPassed;
        this.totalFailed = totalFailed;
        this.totalSkipped = totalSkipped;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public boolean isReviewOnly() {
        return reviewOnly;
    }

    public void setReviewOnly(boolean reviewOnly) {
        this.reviewOnly = reviewOnly;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
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

    public int getTotalPassed() {
        return totalPassed;
    }

    public void setTotalPassed(int totalPassed) {
        this.totalPassed = totalPassed;
    }

    public int getTotalFailed() {
        return totalFailed;
    }

    public void setTotalFailed(int totalFailed) {
        this.totalFailed = totalFailed;
    }

    public int getTotalSkipped() {
        return totalSkipped;
    }

    public void setTotalSkipped(int totalSkipped) {
        this.totalSkipped = totalSkipped;
    }
}
