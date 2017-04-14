package stelztech.youknowehv4.model;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Card {


    private String cardId;
    private String question;
    private String answer;
    private String moreInfo;
    private String dateCreated;
    private String dateModified;

    public Card(String cardId, String question, String answer, String moreInfo, String dateCreated,
                String dateModified) {
        this.cardId = cardId;
        this.question = question;
        this.answer = answer;
        this.moreInfo = moreInfo;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;

    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }
}
