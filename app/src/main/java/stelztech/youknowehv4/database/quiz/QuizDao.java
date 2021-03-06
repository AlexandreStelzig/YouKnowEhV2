package stelztech.youknowehv4.database.quiz;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.quizcard.QuizCard;
import stelztech.youknowehv4.utilities.DateUtilities;

/**
 * Created by alex on 10/14/2017.
 */

public class QuizDao extends DbContentProvider implements IQuizDao, IQuizSchema {

    private Cursor cursor;

    public QuizDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public List<Quiz> fetchAllFinishedQuizzesForActiveProfile() {

        int activeProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();

        cursor = super.rawQuery("SELECT * FROM " + QUIZ_TABLE+ " WHERE "
                + COLUMN_PROFILE_ID + "=" + activeProfileId + " AND " +
                COLUMN_STATE + "='" + String.valueOf(Quiz.STATE.FINISHED_QUIZ) + "'", null);

        List<Quiz> quizList = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                quizList.add(cursorToEntity(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return quizList;
    }

    @Override
    public Quiz fetchQuizById(int quizId) {
        cursor = super.rawQuery("SELECT * FROM " + QUIZ_TABLE + " WHERE "
                + COLUMN_QUIZ_ID + "=" + quizId, null);
        Quiz quiz = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                quiz = (cursorToEntity(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return quiz;
    }

    @Override
    public boolean deleteQuiz(int quizId) {

        return super.delete(QUIZ_TABLE, COLUMN_QUIZ_ID
                + "=" + quizId, null) > 0;
    }

    @Override
    public int createQuiz(Quiz.MODE mode, boolean reverse, boolean reviewOnly) {
        ContentValues values = new ContentValues();

        String date = DateUtilities.getDateNowString();

        values.put(COLUMN_DATE_CREATED, date);
        values.put(COLUMN_DATE_FINISHED, "");
        values.put(COLUMN_REVERSE, reverse);
        values.put(COLUMN_REVIEW_ONLY, reviewOnly);
        values.put(COLUMN_MODE, String.valueOf(mode));
        values.put(COLUMN_PROFILE_ID, Database.mUserDao.fetchActiveProfile().getProfileId());
        values.put(COLUMN_NUM_PASSED, 0);
        values.put(COLUMN_NUM_FAILED, 0);
        values.put(COLUMN_NUM_SKIPPED, 0);
        values.put(COLUMN_STATE, String.valueOf(Quiz.STATE.ACTIVE));

        try {
            return super.insert(QUIZ_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }

    @Override
    public int createQuiz(Quiz.MODE mode, boolean reverse, int nbPassed, int nbFailed, int nbSkipped, String dateCreate, String dateFinished, Quiz.STATE quizState) {
        ContentValues values = new ContentValues();


        values.put(COLUMN_DATE_CREATED, dateCreate);
        values.put(COLUMN_DATE_FINISHED, dateFinished);
        values.put(COLUMN_REVERSE, reverse);
        values.put(COLUMN_REVIEW_ONLY, 0);
        values.put(COLUMN_MODE, String.valueOf(mode));
        values.put(COLUMN_PROFILE_ID, Database.mUserDao.fetchActiveProfile().getProfileId());
        values.put(COLUMN_NUM_PASSED, nbPassed);
        values.put(COLUMN_NUM_FAILED, nbFailed);
        values.put(COLUMN_NUM_SKIPPED, nbSkipped);
        values.put(COLUMN_STATE, String.valueOf(quizState));

        try {
            return super.insert(QUIZ_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }

    @Override
    public boolean markQuizAsQuizFinished(int quizId) {
        ContentValues values = new ContentValues();

        try {

            String now = DateUtilities.getDateNowString();

            values.put(COLUMN_DATE_FINISHED, now);
            values.put(COLUMN_STATE, String.valueOf(Quiz.STATE.FINISHED_QUIZ));

            return super.update(QUIZ_TABLE, values, COLUMN_QUIZ_ID + "=" + quizId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean markQuizAsRoundFinished(int quizId) {
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_STATE, String.valueOf(Quiz.STATE.FINISHED_ROUND));

            return super.update(QUIZ_TABLE, values, COLUMN_QUIZ_ID + "=" + quizId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean markQuizAsActive(int quizId) {
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_STATE, String.valueOf(Quiz.STATE.ACTIVE));

            return super.update(QUIZ_TABLE, values, COLUMN_QUIZ_ID + "=" + quizId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateTotalQuizStats(int quizId, int totalPassed, int totalFailed, int totalSkipped) {
        ContentValues values = new ContentValues();

        try {

            values.put(COLUMN_NUM_PASSED, totalPassed);
            values.put(COLUMN_NUM_FAILED, totalFailed);
            values.put(COLUMN_NUM_SKIPPED, totalSkipped);

            return super.update(QUIZ_TABLE, values, COLUMN_QUIZ_ID + "=" + quizId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }


    @Override
    protected Quiz cursorToEntity(Cursor cursor) {
        int quizId = cursor.getInt(cursor.getColumnIndex(COLUMN_QUIZ_ID));
        String dateCreated = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));
        String dateFinished = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_FINISHED));
        boolean reverse = cursor.getInt(cursor.getColumnIndex(COLUMN_REVERSE)) > 0;
        String mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
        String state = cursor.getString(cursor.getColumnIndex(COLUMN_STATE));
        int profileId = cursor.getInt(cursor.getColumnIndex(COLUMN_PROFILE_ID));
        boolean reviewOnly = cursor.getInt(cursor.getColumnIndex(COLUMN_REVIEW_ONLY)) > 0;
        int passed = cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_PASSED));
        int failed = cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_FAILED));
        int skipped = cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_SKIPPED));


        return new Quiz(quizId, dateCreated, dateFinished, Quiz.STATE.valueOf(state), Quiz.MODE.valueOf(mode), reverse, reviewOnly, profileId, passed, failed, skipped);
    }
}

