package stelztech.youknowehv4.database.card;

import java.util.Date;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Card {


    private long cardId;
    private String question;
    private String answer;
    private String moreInfo;
    private Date dateCreated;
    private Date dateModified;


    public Card(long cardId, String question, String answer, String moreInfo, Date dateCreated, Date dateModified, boolean archived) {
        this.cardId = cardId;
        this.question = question;
        this.answer = answer;
        this.moreInfo = moreInfo;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.archived = archived;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    private boolean archived;


    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
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

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }
}
