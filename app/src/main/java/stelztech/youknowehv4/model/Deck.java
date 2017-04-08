package stelztech.youknowehv4.model;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Deck {

    private String idDeck;
    private String deckName;
    private String dateCreated;
    private String dateModified;

    Deck() {
    }

    public Deck(String idDeck, String deckName, String dateCreated, String dateModified) {
        this.idDeck = idDeck;
        this.deckName = deckName;
        this.dateModified = dateModified;
        this.dateCreated = dateCreated;
    }


    public String getIdDeck() {
        return idDeck;
    }

    public void setIdDeck(String idDeck) {
        this.idDeck = idDeck;
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
