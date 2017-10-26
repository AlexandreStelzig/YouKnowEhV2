package stelztech.youknowehv4.database.carddeck;

import java.util.Date;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class CardDeck {

    private long deckId;
    private long cardId;
    private boolean isPractice;
    private Date dateCreated;
    private Date practiceToggleDate;

    public CardDeck(long deckId, long cardId, boolean isPractice, Date dateCreated, Date practiceToggleDate) {
        this.deckId = deckId;
        this.cardId = cardId;
        this.isPractice = isPractice;
        this.dateCreated = dateCreated;
        this.practiceToggleDate = practiceToggleDate;
    }

    public Date getPracticeToggleDate() {
        return practiceToggleDate;
    }

    public void setPracticeToggleDate(Date practiceToggleDate) {
        this.practiceToggleDate = practiceToggleDate;
    }

    public long getDeckId() {
        return deckId;
    }

    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }
}
