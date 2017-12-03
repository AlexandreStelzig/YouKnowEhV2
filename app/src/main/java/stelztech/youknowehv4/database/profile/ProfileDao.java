package stelztech.youknowehv4.database.profile;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.helper.DateHelper;
import stelztech.youknowehv4.manager.ThemeManager;

/**
 * Created by alex on 10/14/2017.
 */

public class ProfileDao extends DbContentProvider implements IProfileDao, IProfileSchema {

    private Cursor cursor;

    public ProfileDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public List<Profile> fetchAllProfiles() {
        cursor = super.rawQuery("SELECT * FROM " + PROFILE_TABLE, null);

        List<Profile> profileList = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                profileList.add(cursorToEntity(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        return profileList;
    }

    @Override
    public Profile fetchProfileById(int profileId) {
        cursor = super.rawQuery("SELECT * FROM " + PROFILE_TABLE + " WHERE "
                + COLUMN_PROFILE_ID + "=" + profileId, null);
        Profile profile = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                profile = (cursorToEntity(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return profile;
    }

    @Override
    public boolean deleteProfile(int profileId) {
        List<Card> cardList = Database.mCardDao.fetchAllCards();

        for(Card card: cardList){
            Database.mCardDao.deleteCard(card.getCardId());
        }

        List<Deck> deckList = Database.mDeckDao.fetchAllDecks();

        for(Deck deck: deckList){
            Database.mDeckDao.deleteDeck(deck.getDeckId());
        }

        return super.delete(PROFILE_TABLE, COLUMN_PROFILE_ID
                + "=" + profileId, null) > 0;
    }

    @Override
    public int createProfile(String name) {
        ContentValues values = new ContentValues();

        String date = DateHelper.getDateNowString();

        values.put(COLUMN_PROFILE_NAME, name);
        values.put(COLUMN_DATE_CREATED, date);
        values.put(COLUMN_DATE_MODIFIED, date);
        values.put(COLUMN_QUESTION_LABEL, "Question");
        values.put(COLUMN_ANSWER_LABEL, "Answer");
        values.put(COLUMN_ACTIVE_QUIZ_ID, "");
        values.put(COLUMN_PROFILE_COLOR, String.valueOf(ThemeManager.THEME_COLORS.BLUE));
        values.put(COLUMN_PROFILE_LAST_TIME_OPENED, date);
        values.put(COLUMN_PROFILE_IMAGE, R.drawable.city);

        try {
            return super.insert(PROFILE_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }

    @Override
    public boolean updateProfile(int profileId, String name) {
        ContentValues values = new ContentValues();

        try {
            String date = DateHelper.getDateNowString();

            values.put(COLUMN_PROFILE_NAME, name);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateProfileQuestionLabel(int profileId, String questionLabel) {
        ContentValues values = new ContentValues();

        try {
            String date = DateHelper.getDateNowString();

            values.put(COLUMN_QUESTION_LABEL, questionLabel);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateProfileAnswerLabel(int profileId, String answerLabel) {
        ContentValues values = new ContentValues();

        try {
            String date = DateHelper.getDateNowString();

            values.put(COLUMN_ANSWER_LABEL, answerLabel);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean changeProfileColor(int profileId, ThemeManager.THEME_COLORS themeColor) {
        ContentValues values = new ContentValues();

        try {
            String date = DateHelper.getDateNowString();

            values.put(COLUMN_PROFILE_COLOR, String.valueOf(themeColor));
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateLastTimeOpened(int profileId, String lastTimeOpenedDate) {
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_PROFILE_LAST_TIME_OPENED, lastTimeOpenedDate);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateProfileImage(int profileId, int picture) {
        ContentValues values = new ContentValues();

        try {
            String date = DateHelper.getDateNowString();

            values.put(COLUMN_PROFILE_IMAGE, picture);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    protected Profile cursorToEntity(Cursor cursor) {

        int profileId = cursor.getInt(cursor.getColumnIndex(COLUMN_PROFILE_ID));
        String profileName = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_NAME));
        String dateAdded = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));
        String questionLabel = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_LABEL));
        String answerLabel = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER_LABEL));
        int activeQuizId = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE_QUIZ_ID));
        String profileColor = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_COLOR));
        String lastTimeOpened = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_LAST_TIME_OPENED));
        int profileImage = cursor.getInt(cursor.getColumnIndex(COLUMN_PROFILE_IMAGE));

        ThemeManager.THEME_COLORS profileThemeColor = ThemeManager.THEME_COLORS.valueOf(profileColor);

        return new Profile(profileId, profileName, dateAdded, questionLabel, answerLabel, activeQuizId, profileThemeColor, lastTimeOpened, profileImage);
    }

}
