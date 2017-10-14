package stelztech.youknowehv4.database.carddeck;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.deck.IDeckDao;
import stelztech.youknowehv4.database.deck.IDeckSchema;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class CardDeckDao  extends DbContentProvider implements IDeckDao, IDeckSchema{


    public CardDeckDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<Deck> fetchAllDecks() {
        return null;
    }

    @Override
    public Deck fetchDeckFromId(int deckId) {
        return null;
    }

    @Override
    public boolean deleteDeck(int deckId) {
        return false;
    }

    @Override
    public int createDeck(String deckName) {
        return 0;
    }

    @Override
    public boolean updateDeck(int deckId, String newDeckName) {
        return false;
    }

    @Override
    public void swapDeckPosition(Deck deck1, Deck deck2) {

    }

    @Override
    public void changeDeckPosition(int newPosition, Deck deck) {

    }

    @Override
    protected CardDeck cursorToEntity(Cursor cursor) {
        return null;
    }
}
