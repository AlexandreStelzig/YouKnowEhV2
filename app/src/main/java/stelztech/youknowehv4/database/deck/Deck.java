package stelztech.youknowehv4.database.deck;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Deck {

    private int deckId;
    private String deckName;
    private String dateCreated;
    private String dateModified;
    private int position;
    private int profileId;



    public Deck(int deckId, String deckName, String dateCreated, String dateModified, int position, int profileId) {
        this.deckId = deckId;
        this.deckName = deckName;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.position = position;
        this.profileId = profileId;
    }

    public int getProfileId() {
        return profileId;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
