package stelztech.youknowehv4.database.card;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.database.DbContentProvider;

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
    public Card fetchCardById(int cardId) {
        final String selectionArgs[] = { String.valueOf(cardId) };
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
    public int createCard(String question, String answer, String moreInfo) {
        return 0;
    }

    @Override
    public int createCard(String question, String answer, String moreInfo, String dateCreated, String dateModified) {
        return 0;
    }

    @Override
    public void updateCard(int cardId, String question, String answer, String moreInfo) {

    }

    @Override
    public boolean deleteCard(int cardId) {
        return false;
    }

    @Override
    public boolean toggleArchiveCard(int cardId) {
        return false;
    }

    @Override
    public List<Card> fetchArchivedCards() {
        return null;
    }


    @Override
    protected Card cursorToEntity(Cursor cursor) {
        return null;
    }
}
