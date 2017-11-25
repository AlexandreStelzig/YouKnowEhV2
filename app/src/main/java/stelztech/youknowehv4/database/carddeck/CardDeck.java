package stelztech.youknowehv4.database.carddeck;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class CardDeck {

    public static final int REVIEW_TOGGLE_ID = -1;

    private int deckId;
    private int cardId;
    private boolean isPractice;
    private String dateCreated;
    private String practiceToggleDate;

    public CardDeck(int deckId, int cardId, boolean isPractice, String dateCreated, String practiceToggleDate) {
        this.deckId = deckId;
        this.cardId = cardId;
        this.isPractice = isPractice;
        this.dateCreated = dateCreated;
        this.practiceToggleDate = practiceToggleDate;
    }

    public String getReviewToggleDate() {
        return practiceToggleDate;
    }

    public void setPracticeToggleDate(String practiceToggleDate) {
        this.practiceToggleDate = practiceToggleDate;
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    public boolean isReview() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
