package stelztech.youknowehv4.manager.exportimport;

/**
 * Created by alex on 1/7/2018.
 */

public class CardHolder {
    private String question;
    private String answer;
    private String note;
    private String reviewToggleDate;
    private String dateCreated;
    private String dateModified;

    protected CardHolder(String question, String answer, String note, String reviewToggleDate, String dateCreated, String dateModified) {
        this.question = question;
        this.answer = answer;
        this.note = note;
        this.reviewToggleDate = reviewToggleDate;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
    }

    protected String getDateCreated() {
        return dateCreated;
    }

    protected void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    protected String getDateModified() {
        return dateModified;
    }

    protected void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    protected String getReviewToggleDate() {
        return reviewToggleDate;
    }

    protected void setReviewToggleDate(String reviewToggleDate) {
        this.reviewToggleDate = reviewToggleDate;
    }

    protected String getQuestion() {
        return question;
    }

    protected void setQuestion(String question) {
        this.question = question;
    }

    protected String getAnswer() {
        return answer;
    }

    protected void setAnswer(String answer) {
        this.answer = answer;
    }

    protected String getNote() {
        return note;
    }

    protected void setNote(String note) {
        this.note = note;
    }
}