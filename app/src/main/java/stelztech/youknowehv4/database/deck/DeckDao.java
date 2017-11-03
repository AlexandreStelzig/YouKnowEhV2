package stelztech.youknowehv4.database.deck;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.helper.DateHelper;

/**
 * Created by alex on 10/14/2017.
 */

public class DeckDao extends DbContentProvider implements IDeckDao, IDeckSchema {

    private Cursor cursor;

    public DeckDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<Deck> fetchAllDecks() {
        List<Deck> deckList = new ArrayList<Deck>();
        long activeProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();
        Cursor cursor = super.rawQuery("SELECT * FROM " + DECK_TABLE + " WHERE "
                + COLUMN_PROFILE_ID + "=" + activeProfileId, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Deck deck = cursorToEntity(cursor);
                deckList.add(deck);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return deckList;
    }

    @Override
    public Deck fetchDeckById(long deckId) {
        final String selectionArgs[] = {String.valueOf(deckId)};
        final String selection = COLUMN_DECK_ID + " = ?";
        Deck deck = null;
        cursor = super.query(DECK_TABLE, DECK_COLUMNS, selection,
                selectionArgs, COLUMN_DECK_ID);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                deck = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return deck;
    }

    @Override
    public boolean deleteDeck(long deckId) {

        final String selectionArgs[] = {String.valueOf(deckId)};
        final String selection = COLUMN_DECK_ID + " = ?";

        try {
            List<Card> cards = Database.mCardDeckDao.fetchCardsByDeckId(deckId);
            // delete card-deck
            for (int counter = 0; counter < cards.size(); counter++) {
                Database.mCardDeckDao.deleteCardDeck(cards.get(counter).getCardId(), deckId);
            }

            // resets the positions when deleting a deck
            List<Deck> deckList = fetchAllDecks();
            int deckToDeletePosition = fetchDeckById(deckId).getPosition();
            ContentValues values = new ContentValues();

            for (int i = 0; i < deckList.size(); i++) {
                if (deckList.get(i).getPosition() > deckToDeletePosition) {
                    values.put(COLUMN_POSITION, deckList.get(i).getPosition() - 1);
                    super.update(DECK_TABLE, values,
                            COLUMN_DECK_ID + "=" + deckList.get(i).getDeckId(), null);
                }
            }

            return super.delete(DECK_TABLE, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public long createDeck(String deckName) {
        ContentValues values = new ContentValues();

        String date = DateHelper.getDateNow();

        long activeProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();
        int position = fetchAllDecks().size() + 1; // TODO change ,size to SQL method

        values.put(COLUMN_DECK_NAME, deckName);
        values.put(COLUMN_DATE_CREATED, date);
        values.put(COLUMN_DATE_MODIFIED, date);
        values.put(COLUMN_PROFILE_ID, activeProfileId);
        values.put(COLUMN_POSITION, position);

        try {
            return super.insert(DECK_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }

    @Override
    public boolean updateDeck(long deckId, String newDeckName) {
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

        long deckId = cursor.getLong(cursor.getColumnIndex(COLUMN_DECK_ID));
        long profileId = cursor.getLong(cursor.getColumnIndex(COLUMN_PROFILE_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_DECK_NAME));
        Date dateCreated = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_CREATED)));
        Date dateModified = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_MODIFIED)));
        int position = cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION));

        return new Deck(deckId, name, dateCreated, dateModified, position, profileId);
    }

}
