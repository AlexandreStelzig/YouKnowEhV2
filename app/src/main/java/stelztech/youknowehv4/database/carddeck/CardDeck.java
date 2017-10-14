package stelztech.youknowehv4.database.carddeck;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class CardDeck {

    private int deckId;
    private int cardId;
    private boolean isPractice;
    private String dateAdded;
    private String practiceToggleDate;

    public CardDeck(int deckId, int cardId, boolean isPractice, String dateAdded, String practiceToggleDate) {
        this.deckId = deckId;
        this.cardId = cardId;
        this.isPractice = isPractice;
        this.dateAdded = dateAdded;
        this.practiceToggleDate = practiceToggleDate;
    }

    public String getPracticeToggleDate() {
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

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
