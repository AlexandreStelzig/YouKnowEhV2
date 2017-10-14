package stelztech.youknowehv4.database.card;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import stelztech.youknowehv4.database.DbContentProvider;

/**
 * Created by alex on 10/14/2017.
 */

public class CardDao extends DbContentProvider implements ICardDao, ICardSchema {

    public CardDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public List<Card> fetchAllCards() {
        return null;
    }

    @Override
    public List<Card> fetchCardById(int cardId) {
        return null;
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
