package stelztech.youknowehv4.database.profile;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import stelztech.youknowehv4.R;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.utilities.BitmapUtilities;
import stelztech.youknowehv4.utilities.DateUtilities;
import stelztech.youknowehv4.manager.ThemeManager;

import static stelztech.youknowehv4.database.profile.Profile.NO_QUIZ;

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

        for (Card card : cardList) {
            Database.mCardDao.deleteCard(card.getCardId());
        }

        List<Deck> deckList = Database.mDeckDao.fetchAllDecks();

        for (Deck deck : deckList) {
            Database.mDeckDao.deleteDeck(deck.getDeckId());
        }

        return super.delete(PROFILE_TABLE, COLUMN_PROFILE_ID
                + "=" + profileId, null) > 0;
    }

    @Override
    public int createProfile(String name, String frontLabel, String backLabel, String imagePath, ThemeManager.THEME_COLORS themeColor, Profile.PROFILE_TYPE profileType ) {
        ContentValues values = new ContentValues();

        String date = DateUtilities.getDateNowString();

        values.put(COLUMN_PROFILE_NAME, name);
        values.put(COLUMN_DATE_CREATED, date);
        values.put(COLUMN_DATE_MODIFIED, date);
        values.put(COLUMN_QUESTION_LABEL, frontLabel);
        values.put(COLUMN_ANSWER_LABEL, backLabel);
        values.put(COLUMN_ACTIVE_QUIZ_ID, NO_QUIZ);
        values.put(COLUMN_PROFILE_COLOR, String.valueOf(themeColor));
        values.put(COLUMN_PROFILE_LAST_TIME_OPENED, date);
        values.put(COLUMN_PROFILE_IMAGE, imagePath);
        values.put(COLUMN_DEFAULT_SORTING, 0);
        values.put(COLUMN_DISPLAY_ALL_CARDS, 1);
        values.put(COLUMN_DISPLAY_SPECIFIC_DECK, 1);
        values.put(COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED, 0);
        values.put(COLUMN_QUICK_TOGGLE_REVIEW, 12);
        values.put(COLUMN_PROFILE_TYPE, String.valueOf(profileType));

        try {
            return super.insert(PROFILE_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }

    @Override
    public boolean updateProfileName(int profileId, String name) {
        ContentValues values = new ContentValues();

        try {
            String date = DateUtilities.getDateNowString();

            values.put(COLUMN_PROFILE_NAME, name);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateProfileFrontLabel(int profileId, String questionLabel) {
        ContentValues values = new ContentValues();

        try {
            String date = DateUtilities.getDateNowString();

            values.put(COLUMN_QUESTION_LABEL, questionLabel);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateProfileBackLabel(int profileId, String answerLabel) {
        ContentValues values = new ContentValues();

        try {
            String date = DateUtilities.getDateNowString();

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
            String date = DateUtilities.getDateNowString();

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
    public boolean updateProfileImage(int profileId, String imagePath) {
        ContentValues values = new ContentValues();

        try {
            String date = DateUtilities.getDateNowString();

            values.put(COLUMN_PROFILE_IMAGE, imagePath);
            values.put(COLUMN_DATE_MODIFIED, date);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public int fetchActiveQuizId() {
        return Database.mUserDao.fetchActiveProfile().getActiveQuizId();
    }

    @Override
    public boolean setActiveQuizId(int profileId, int quizId) {
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_ACTIVE_QUIZ_ID, quizId);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + profileId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }


    @Override
    public boolean toggleDisplayNumDecksAllCards() {
        ContentValues values = new ContentValues();

        try {

            Profile currentProfile = Database.mUserDao.fetchActiveProfile();

            boolean currentDisplay = currentProfile.isDisplayNbDecksAllCards();
            boolean nextDisplay = !currentDisplay;

            values.put(COLUMN_DISPLAY_ALL_CARDS, nextDisplay);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + currentProfile.getProfileId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleDisplayNumDecksSpecificCard() {
        ContentValues values = new ContentValues();

        try {

            Profile currentProfile = Database.mUserDao.fetchActiveProfile();

            boolean currentDisplay = currentProfile.isDisplayNbDecksSpecificCards();
            boolean nextDisplay = !currentDisplay;

            values.put(COLUMN_DISPLAY_SPECIFIC_DECK, nextDisplay);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + currentProfile.getProfileId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }


    @Override
    public boolean toggleAllowSearchOnQueryChanged() {
        ContentValues values = new ContentValues();

        try {
            Profile currentProfile = Database.mUserDao.fetchActiveProfile();

            boolean currentAllowOnQueryChange = currentProfile.isAllowOnQueryChanged();
            boolean nextAllowOnQueryChange = !currentAllowOnQueryChange;

            values.put(COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED, nextAllowOnQueryChange);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + currentProfile.getProfileId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }


    @Override
    public boolean updateDefaultSortPosition(int position) {
        Profile currentProfile = Database.mUserDao.fetchActiveProfile();

        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_DEFAULT_SORTING, position);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + currentProfile.getProfileId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateQuickToggleReviewHours(int hours) {
        Profile currentProfile = Database.mUserDao.fetchActiveProfile();

        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_QUICK_TOGGLE_REVIEW, hours);

            return super.update(PROFILE_TABLE, values, COLUMN_PROFILE_ID + "=" + currentProfile.getProfileId(), null) > 0;
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
        String profileTypeString = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_TYPE));
        String lastTimeOpened = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_LAST_TIME_OPENED));
        String profileImagePath = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_IMAGE));
        int defaultSorting = cursor.getInt(cursor.getColumnIndex(COLUMN_DEFAULT_SORTING));
        boolean displayAllCards = cursor.getInt(cursor.getColumnIndex(COLUMN_DISPLAY_ALL_CARDS)) > 0;
        boolean displaySpecificDeck = cursor.getInt(cursor.getColumnIndex(COLUMN_DISPLAY_SPECIFIC_DECK)) > 0;
        boolean allowOnQueryChanged = cursor.getInt(cursor.getColumnIndex(COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED)) > 0;
        int quickToggle = cursor.getInt(cursor.getColumnIndex(COLUMN_QUICK_TOGGLE_REVIEW));

        ThemeManager.THEME_COLORS profileThemeColor = ThemeManager.THEME_COLORS.valueOf(profileColor);
        Profile.PROFILE_TYPE profileType = Profile.PROFILE_TYPE.valueOf(profileTypeString);

        return new Profile(profileId, profileName, dateAdded, questionLabel, answerLabel, activeQuizId, profileThemeColor, profileType, lastTimeOpened, profileImagePath,
                displayAllCards, displaySpecificDeck, allowOnQueryChanged, defaultSorting, quickToggle);
    }



}
