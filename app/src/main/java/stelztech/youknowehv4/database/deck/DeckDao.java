package stelztech.youknowehv4.database.deck;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.card.ICardDao;
import stelztech.youknowehv4.database.card.ICardSchema;

/**
 * Created by alex on 10/14/2017.
 */

public class DeckDao extends DbContentProvider implements IDeckDao, IDeckSchema {


    public DeckDao(SQLiteDatabase db) {
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
    protected Deck cursorToEntity(Cursor cursor) {
        return null;
    }
}
