package Model;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class WordDeck {

    private String idDeck;
    private String idWord;
    private boolean isPractice;
    private String dateAdded;

    WordDeck() {
    }

    public WordDeck(String idDeck, String idWord, boolean isPractice, String dateAdded) {
        this.idDeck = idDeck;
        this.idWord = idWord;
        this.isPractice = isPractice;
        this.dateAdded = dateAdded;
    }


    public String getIdDeck() {
        return idDeck;
    }

    public void setIdDeck(String idDeck) {
        this.idDeck = idDeck;
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

    public String getIdWord() {
        return idWord;
    }

    public void setIdWord(String idWord) {
        this.idWord = idWord;
    }
}
