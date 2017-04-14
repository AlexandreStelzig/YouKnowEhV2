package stelztech.youknowehv4.model;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Deck {

    private String deckId;
    private String deckName;
    private String dateCreated;
    private String dateModified;

    Deck() {
    }

    public Deck(String deckId, String deckName, String dateCreated, String dateModified) {
        this.deckId = deckId;
        this.deckName = deckName;
        this.dateModified = dateModified;
        this.dateCreated = dateCreated;
    }


    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
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
