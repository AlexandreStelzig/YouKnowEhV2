package stelztech.youknowehv4.database.deck;

import java.util.Date;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Deck {

    private int deckId;
    private String deckName;
    private Date dateCreated;
    private Date dateModified;



    private int position;

    Deck() {
    }

    public Deck(int deckId, String deckName, Date dateCreated, Date dateModified, int position) {
        this.deckId = deckId;
        this.deckName = deckName;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.position = position;
    }

    public int getDeckId() {
        return deckId;
    }

    public void setDeckId(int deckId) {
        this.deckId = deckId;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
