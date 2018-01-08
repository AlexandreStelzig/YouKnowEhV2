package stelztech.youknowehv4.database.card;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.utilities.DateUtilities;
import stelztech.youknowehv4.manager.SortingStateManager;

/**
 * Created by alex on 10/14/2017.
 */

public class CardDao extends DbContentProvider implements ICardDao, ICardSchema {

    private Cursor cursor;

    public CardDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public List<Card> fetchAllCards() {

        int activeProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();
        cursor = super.rawQuery("SELECT * FROM " + CARD_TABLE + " WHERE "
                + COLUMN_ARCHIVED + "=" + 0 + " AND "
                + COLUMN_PROFILE_ID + "=" + activeProfileId, null);

        List<Card> cardList = new ArrayList<Card>();

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Card card = cursorToEntity(cursor);
                cardList.add(card);
                cursor.moveToNext();
            }
            cursor.close();
        }

        cardList = SortingStateManager.getInstance().sortCardList(cardList);

        return cardList;
    }

    @Override
    public Card fetchCardById(int cardId) {

        Card card = null;
        cursor = super.rawQuery("SELECT * FROM " + CARD_TABLE + " WHERE "
                + COLUMN_CARD_ID + "=" + cardId, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                card = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return card;
    }

    @Override
    public int createCard(String question, String answer, String moreInfo) {

        ContentValues values = new ContentValues();

        String date = DateUtilities.getDateNowString();

        long activeProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();


        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_ANSWER, answer);
        values.put(COLUMN_MORE_INFORMATION, moreInfo);
        values.put(COLUMN_DATE_CREATED, date);
        values.put(COLUMN_DATE_MODIFIED, date);
        values.put(COLUMN_PROFILE_ID, activeProfileId);
        values.put(COLUMN_ARCHIVED, false);

        try {
            return super.insert(CARD_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }

    @Override
    public int createCard(String question, String answer, String moreInfo, String dateCreate, String dateModified) {
        ContentValues values = new ContentValues();

        long activeProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();


        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_ANSWER, answer);
        values.put(COLUMN_MORE_INFORMATION, moreInfo);
        values.put(COLUMN_DATE_CREATED, dateCreate);
        values.put(COLUMN_DATE_MODIFIED, dateModified);
        values.put(COLUMN_PROFILE_ID, activeProfileId);
        values.put(COLUMN_ARCHIVED, false);

        try {
            return super.insert(CARD_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }


    @Override
    public boolean updateCard(int cardId, String question, String answer, String moreInfo) {

        ContentValues values = new ContentValues();

        try {
            String date = DateUtilities.getDateNowString();

            values.put(COLUMN_QUESTION, question);
            values.put(COLUMN_ANSWER, answer);
            values.put(COLUMN_MORE_INFORMATION, moreInfo);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(CARD_TABLE, values, COLUMN_CARD_ID + "=" + cardId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCard(int cardId) {

        try {
            List<Deck> decks = Database.mCardDeckDao.fetchDecksByCardId(cardId);
            // delete all card-deck for this card
            for (int counter = 0; counter < decks.size(); counter++) {
                Database.mCardDeckDao.deleteCardDeck(cardId, decks.get(counter).getDeckId());
            }

            Log.d("database", "delete cardId " + cardId );

            return super.delete(CARD_TABLE, COLUMN_CARD_ID + "=" + cardId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleArchiveCard(int cardId) {

        ContentValues values = new ContentValues();

        try {
            boolean currentArchive = fetchCardById(cardId).isArchived();
            if (!currentArchive) {
                List<Deck> decks = Database.mCardDeckDao.fetchDecksByCardId(cardId);
                // delete all card-deck for this card
                for (int counter = 0; counter < decks.size(); counter++) {
                    Database.mCardDeckDao.deleteCardDeck(cardId, decks.get(counter).getDeckId());
                }
            }

            boolean nextArchive = !currentArchive;

            values.put(COLUMN_ARCHIVED, nextArchive);
            return super.update(CARD_TABLE, values, COLUMN_CARD_ID + "=" + cardId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public List<Card> fetchArchivedCards() {

        long activeProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();

        cursor = super.rawQuery("SELECT * FROM " + CARD_TABLE + " WHERE "
                + COLUMN_ARCHIVED + "=" + 1 + " AND "
                + COLUMN_PROFILE_ID + "=" + activeProfileId);

        List<Card> cardList = new ArrayList<Card>();

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Card card = cursorToEntity(cursor);
                cardList.add(card);
                cursor.moveToNext();
            }
            cursor.close();
        }

        cardList = SortingStateManager.getInstance().sortCardList(cardList);

        return cardList;
    }

    @Override
    public int fetchNumberOfCardsByProfileId(int profileId) {
        return (int) DatabaseUtils.longForQuery(mDb, "SELECT COUNT(*) FROM " + CARD_TABLE + " WHERE "
                + COLUMN_PROFILE_ID + "=" + profileId, null);
    }


    @Override
    protected Card cursorToEntity(Cursor cursor) {

        int cardId = cursor.getInt(cursor.getColumnIndex(COLUMN_CARD_ID));
        String question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION));
        String answer = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER));
        String moreInfo = cursor.getString(cursor.getColumnIndex(COLUMN_MORE_INFORMATION));
        String dateCreated = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));
        String dateModified = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_MODIFIED));
        boolean archived = cursor.getInt(cursor.getColumnIndex(COLUMN_ARCHIVED)) > 0;

        return new Card(cardId, question, answer, moreInfo, dateCreated, dateModified, archived);


    }
}
