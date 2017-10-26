package stelztech.youknowehv4.database.card;

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
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.helper.DateHelper;

/**
 * Created by alex on 10/14/2017.
 */

public class CardDao extends DbContentProvider implements ICardDao, ICardSchema {

    private ContentValues initialValues;
    private Cursor cursor;

    public CardDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public List<Card> fetchAllCards() {

        List<Card> cardList = new ArrayList<Card>();
        cursor = super.query(CARD_TABLE, CARD_COLUMNS, null, null, COLUMN_CARD_ID);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Card card = cursorToEntity(cursor);
                cardList.add(card);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return cardList;
    }

    @Override
    public Card fetchCardById(long cardId) {
        final String selectionArgs[] = {String.valueOf(cardId)};
        final String selection = COLUMN_CARD_ID + " = ?";
        Card card = null;
        cursor = super.query(CARD_TABLE, CARD_COLUMNS, selection,
                selectionArgs, COLUMN_CARD_ID);
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
    public long createCard(String question, String answer, String moreInfo) {

        ContentValues values = new ContentValues();

        String date = DateHelper.getDateNow();

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
    public boolean updateCard(long cardId, String question, String answer, String moreInfo) {
        final String selectionArgs[] = {String.valueOf(cardId)};
        final String selection = COLUMN_CARD_ID + " = ?";

        ContentValues values = new ContentValues();

        try {
            String date = DateHelper.getDateNow();

            values.put(COLUMN_QUESTION, question);
            values.put(COLUMN_ANSWER, answer);
            values.put(COLUMN_MORE_INFORMATION, moreInfo);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(CARD_TABLE, values, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteCard(long cardId) {

        final String selectionArgs[] = {String.valueOf(cardId)};
        final String selection = COLUMN_CARD_ID + " = ?";

        try {
            List<Deck> decks = Database.mCardDeckDao.fetchDecksByCardId(cardId);
            // delete all card-deck for this card
            for (int counter = 0; counter < decks.size(); counter++) {
                Database.mCardDeckDao.deleteCardDeck(cardId, decks.get(counter).getDeckId());
            }
            return super.delete(CARD_TABLE, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleArchiveCard(long cardId) {
        final String selectionArgs[] = {String.valueOf(cardId)};
        final String selection = COLUMN_CARD_ID + " = ?";

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
            values.put(COLUMN_ARCHIVED, !currentArchive);
            return super.update(CARD_TABLE, values, selection, selectionArgs) > 0;
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

        return cardList;
    }


    @Override
    protected Card cursorToEntity(Cursor cursor) {

        long cardId = cursor.getLong(cursor.getColumnIndex(COLUMN_CARD_ID));
        String question = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION));
        String answer = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER));
        String moreInfo = cursor.getString(cursor.getColumnIndex(COLUMN_MORE_INFORMATION));
        Date dateCreated = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_CREATED)));
        Date dateModified = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_MODIFIED)));
        boolean archived = cursor.getInt(cursor.getColumnIndex(COLUMN_ARCHIVED)) > 0;

        return new Card(cardId, question, answer, moreInfo, dateCreated, dateModified, archived);


    }
}
