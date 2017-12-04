package stelztech.youknowehv4.database.quizcard;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.database.DbContentProvider;

/**
 * Created by alex on 10/14/2017.
 */

public class QuizCardDao extends DbContentProvider implements IQuizCardDao, IQuizCardSchema {


    private Cursor cursor;

    public QuizCardDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public QuizCard fetchQuizCardById(int cardId, int quizId) {

        cursor = super.rawQuery("SELECT * FROM " + QUIZ_CARD_TABLE + " WHERE " +
                COLUMN_CARD_ID + "=" + cardId + " AND " + COLUMN_QUIZ_ID + "=" + quizId, null);

        QuizCard quizCard = null;

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                quizCard = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return quizCard;
    }

    @Override
    public List<QuizCard> fetchQuizCardsByQuizId(int quizId) {
        cursor = super.rawQuery("SELECT * FROM " + QUIZ_CARD_TABLE +
                " WHERE " + COLUMN_QUIZ_ID + "=" + quizId, null);

        // get all card-deck
        List<QuizCard> quizCards = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    quizCards.add(cursorToEntity(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        return quizCards;
    }

    @Override
    public boolean deleteQuizCard(int cardId, int quizId) {
        return super.delete(QUIZ_CARD_TABLE, COLUMN_CARD_ID + "=" + cardId +
                " AND " + COLUMN_QUIZ_ID + "=" + quizId, null) > 0;
    }

    @Override
    public int createQuizCard(int cardId, int quizId, String question, String answer, int position) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_CARD_ID, cardId);
        values.put(COLUMN_QUIZ_ID, quizId);
        values.put(COLUMN_CARD_QUESTION, question);
        values.put(COLUMN_CARD_ANSWER, answer);
        values.put(COLUMN_NUMBER_FAILED, 0);
        values.put(COLUMN_PASSED, false);
        values.put(COLUMN_POSITION, position);

        try {
            return super.insert(QUIZ_CARD_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }

    @Override
    public boolean incrementNumberFailed(int cardId, int quizId) {
        ContentValues values = new ContentValues();

        try {
            int numberFailed = fetchQuizCardById(cardId, quizId).getNumFailed() + 1;

            values.put(COLUMN_NUMBER_FAILED, numberFailed);

            return super.update(QUIZ_CARD_TABLE, values, COLUMN_CARD_ID + "="
                    + cardId + " AND " + COLUMN_QUIZ_ID + "=" + quizId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean markCardAsPassed(int cardId, int quizId) {
        ContentValues values = new ContentValues();

        try {

            values.put(COLUMN_PASSED, true);

            return super.update(QUIZ_CARD_TABLE, values, COLUMN_CARD_ID + "="
                    + cardId + " AND " + COLUMN_QUIZ_ID + "=" + quizId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    protected QuizCard cursorToEntity(Cursor cursor) {

        int quizId = cursor.getInt(cursor.getColumnIndex(COLUMN_QUIZ_ID));
        int cardId = cursor.getInt(cursor.getColumnIndex(COLUMN_CARD_ID));
        int numFailed = cursor.getInt(cursor.getColumnIndex(COLUMN_NUMBER_FAILED));
        boolean passed = cursor.getInt(cursor.getColumnIndex(COLUMN_PASSED)) > 0;
        int position = cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION));
        String question = cursor.getString(cursor.getColumnIndex(COLUMN_CARD_QUESTION));
        String answer = cursor.getString(cursor.getColumnIndex(COLUMN_CARD_ANSWER));

        return new QuizCard(quizId, cardId, question, answer, passed, numFailed, position);
    }
}
