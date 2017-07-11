package stelztech.youknowehv4.model;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class CardDeck {

    private String deckId;
    private String cardId;
    private boolean isPractice;
    private String dateAdded;
    private String practiceToggleDate;

    CardDeck() {
    }

    public CardDeck(String deckId, String cardId, boolean isPractice, String dateAdded, String practiceToggleDate) {
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

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
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

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
