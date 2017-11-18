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
import stelztech.youknowehv4.manager.SortingStateManager;

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

        deckList = SortingStateManager.getInstance().sortDeck(deckList);

        return deckList;
    }

    @Override
    public Deck fetchDeckById(int deckId) {
        Deck deck = null;
        cursor = super.rawQuery("SELECT * FROM " + DECK_TABLE + " WHERE "
                + COLUMN_DECK_ID + "=" + deckId, null);

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
    public boolean deleteDeck(int deckId) {

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

            return super.delete(DECK_TABLE, COLUMN_DECK_ID + "=" + deckId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public int createDeck(String deckName) {
        ContentValues values = new ContentValues();

        String date = DateHelper.getDateNowString();

        long activeProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();
        int position = fetchAllDecks().size() + 1; // TODO create SQL method to fetch deck.size()

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
    public boolean updateDeck(int deckId, String newDeckName) {
        ContentValues values = new ContentValues();

        try {

            String date = DateHelper.getDateNowString();

            values.put(COLUMN_DECK_NAME, newDeckName);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(DECK_TABLE, values, COLUMN_DECK_ID + "=" + deckId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean swapDeckPosition(Deck deck1, Deck deck2) {
        ContentValues values = new ContentValues();

        try {

            int deck1Position = deck1.getPosition();
            int deck2Position = deck2.getPosition();

            values.put(COLUMN_POSITION, deck2Position);

            super.update(DECK_TABLE, values, COLUMN_DECK_ID + "=" + deck1.getDeckId(), null);

            values.put(COLUMN_POSITION, deck1Position);
            return super.update(DECK_TABLE, values, COLUMN_DECK_ID + "=" + deck2.getDeckId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean changeDeckPosition(int newPosition, Deck deck) {
        ContentValues values = new ContentValues();

        try {
            List<Deck> deckList = fetchAllDecks();


            values.put(COLUMN_POSITION, newPosition);
            super.update(DECK_TABLE, values, COLUMN_DECK_ID + "=" + deck.getDeckId(), null);

            for (int i = 0; i < deckList.size(); i++) {
                int deckTempPosition = deckList.get(i).getPosition();
                if (deck.getPosition() < newPosition) {
                    if (deckTempPosition > deck.getPosition() && deckTempPosition <= newPosition) {
                        values.put(COLUMN_POSITION, deckTempPosition - 1);
                        super.update(DECK_TABLE, values, COLUMN_DECK_ID + "=" + deckList.get(i).getDeckId(), null);
                    }
                } else {
                    if (deckTempPosition < deck.getPosition() && deckTempPosition >= newPosition) {
                        values.put(COLUMN_POSITION, deckTempPosition + 1);
                        super.update(DECK_TABLE, values, COLUMN_DECK_ID + "=" + deckList.get(i).getDeckId(), null);
                    }
                }
            }
            return true;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }


    @Override
    protected Deck cursorToEntity(Cursor cursor) {

        int deckId = cursor.getInt(cursor.getColumnIndex(COLUMN_DECK_ID));
        int profileId = cursor.getInt(cursor.getColumnIndex(COLUMN_PROFILE_ID));
        String name = cursor.getString(cursor.getColumnIndex(COLUMN_DECK_NAME));
        String dateCreated = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));
        String dateModified = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_MODIFIED));
        int position = cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION));

        return new Deck(deckId, name, dateCreated, dateModified, position, profileId);
    }

}
