package stelztech.youknowehv4.manager.exportimport;

/**
 * Created by alex on 1/7/2018.
 */

public class QuizHistoryHolder {

    private String quizMode;
    private String orientationReverse;
    private String numberPassed;
    private String numberFailed;
    private String numberSkipped;
    private String dateCreated;
    private String dateFinished;


    protected QuizHistoryHolder(String quizMode, String orientationReverse, String numberPassed, String numberFailed, String numberSkipped, String dateCreated, String dateFinished) {
        this.quizMode = quizMode;
        this.orientationReverse = orientationReverse;
        this.numberPassed = numberPassed;
        this.numberFailed = numberFailed;
        this.numberSkipped = numberSkipped;
        this.dateCreated = dateCreated;
        this.dateFinished = dateFinished;
    }

    protected String getQuizMode() {
        return quizMode;
    }

    protected void setQuizMode(String quizMode) {
        this.quizMode = quizMode;
    }

    protected String getOrientationReverse() {
        return orientationReverse;
    }

    protected void setOrientationReverse(String orientationReverse) {
        this.orientationReverse = orientationReverse;
    }

    protected String getNumberPassed() {
        return numberPassed;
    }

    protected void setNumberPassed(String numberPassed) {
        this.numberPassed = numberPassed;
    }

    protected String getNumberFailed() {
        return numberFailed;
    }

    protected void setNumberFailed(String numberFailed) {
        this.numberFailed = numberFailed;
    }

    protected String getNumberSkipped() {
        return numberSkipped;
    }

    protected void setNumberSkipped(String numberSkipped) {
        this.numberSkipped = numberSkipped;
    }

    protected String getDateCreated() {
        return dateCreated;
    }

    protected void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    protected String getDateFinished() {
        return dateFinished;
    }

    protected void setDateFinished(String dateFinished) {
        this.dateFinished = dateFinished;
    }
}
