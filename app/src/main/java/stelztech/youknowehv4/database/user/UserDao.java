package stelztech.youknowehv4.database.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.helper.DateHelper;

/**
 * Created by alex on 10/14/2017.
 */

public class UserDao extends DbContentProvider implements IUserDao, IUserSchema {

    private Cursor cursor;

    public UserDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public User fetchUser() {

        cursor = super.rawQuery("SELECT * FROM " + USER_TABLE, null);
        User user = null;

        if (cursor.moveToFirst()) {
            user = (cursorToEntity(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return user;
    }

    @Override
    public int createUser() {

        if (fetchUser() == null) {
            ContentValues values = new ContentValues();

            String date = DateHelper.getDateNowString();


            // TODO move default values to config file
            values.put(COLUMN_DATE_CREATED, date);
            values.put(COLUMN_ACTIVE_PROFILE_ID, -1);
            values.put(COLUMN_DEFAULT_SORTING, 0);
            values.put(COLUMN_DISPLAY_ALL_CARDS, 1);
            values.put(COLUMN_DISPLAY_SPECIFIC_DECK, 0);
            values.put(COLUMN_ALLOW_REVIEW_ALL, 0);
            values.put(COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED, 1);
            values.put(COLUMN_QUICK_TOGGLE_REVIEW, 12);

            try {
                return super.insert(USER_TABLE, values);
            } catch (SQLiteConstraintException ex) {
                Log.w("Database", ex.getMessage());
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public boolean updateDefaultSortPosition(int position) {
        final String selectionArgs[] = {String.valueOf(fetchUser().getUserId())};
        final String selection = COLUMN_USER_ID + " = ?";

        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_DEFAULT_SORTING, position);

            return super.update(USER_TABLE, values, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateQuickToggleReviewHours(int hours) {
        final String selectionArgs[] = {String.valueOf(fetchUser().getUserId())};
        final String selection = COLUMN_USER_ID + " = ?";

        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_QUICK_TOGGLE_REVIEW, hours);

            return super.update(USER_TABLE, values, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean setActiveProfile(int profileId) {
        ContentValues values = new ContentValues();

        try {

            long userId = fetchUser().getUserId();

            values.put(COLUMN_ACTIVE_PROFILE_ID, profileId);

            return super.update(USER_TABLE, values, COLUMN_USER_ID + "=" + userId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleReviewAllCards() {
        ContentValues values = new ContentValues();

        try {
            User user = fetchUser();

            boolean current = user.isAllowPracticeAll();
            boolean next = !current;

            values.put(COLUMN_ALLOW_REVIEW_ALL, next);

            return super.update(USER_TABLE, values, COLUMN_USER_ID + "=" + user.getUserId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleAllowSearchOnQueryChanged() {
        ContentValues values = new ContentValues();

        try {
            User user = fetchUser();

            boolean currentAllowOnQueryChange = user.isAllowOnQueryChanged();
            boolean nextAllowOnQueryChange = !currentAllowOnQueryChange;

            values.put(COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED, nextAllowOnQueryChange);

            return super.update(USER_TABLE, values, COLUMN_USER_ID + "=" + user.getUserId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public Profile fetchActiveProfile() {
        return Database.mProfileDao.fetchProfileById(fetchUser().getActiveProfileId());
    }

    @Override
    public boolean toggleDisplayNumDecksAllCards() {
        ContentValues values = new ContentValues();

        try {

            User user = fetchUser();

            boolean currentDisplay = user.isDisplayNbDecksAllCards();
            boolean nextDisplay = !currentDisplay;

            values.put(COLUMN_DISPLAY_ALL_CARDS, nextDisplay);

            return super.update(USER_TABLE, values, COLUMN_USER_ID + "=" + user.getUserId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleDisplayNumDecksSpecificCard() {
        ContentValues values = new ContentValues();

        try {

            User user = fetchUser();

            boolean currentDisplay = user.isDisplayNbDecksSpecificCards();
            boolean nextDisplay = !currentDisplay;

            values.put(COLUMN_DISPLAY_ALL_CARDS, nextDisplay);

            return super.update(USER_TABLE, values, COLUMN_USER_ID + "=" + user.getUserId(), null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    protected User cursorToEntity(Cursor cursor) {
        int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
        int activeProfileId = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE_PROFILE_ID));
        String dateCreated = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));
        int defaultSorting = cursor.getInt(cursor.getColumnIndex(COLUMN_DEFAULT_SORTING));
        boolean displayAllCards = cursor.getInt(cursor.getColumnIndex(COLUMN_DISPLAY_ALL_CARDS)) > 0;
        boolean displaySpecificDeck = cursor.getInt(cursor.getColumnIndex(COLUMN_DISPLAY_SPECIFIC_DECK)) > 0;
        boolean allowPracticeAll = cursor.getInt(cursor.getColumnIndex(COLUMN_ALLOW_REVIEW_ALL)) > 0;
        boolean allowOnQueryChanged = cursor.getInt(cursor.getColumnIndex(COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED)) > 0;
        int quickToggle = cursor.getInt(cursor.getColumnIndex(COLUMN_QUICK_TOGGLE_REVIEW));


        return new User(userId, dateCreated, activeProfileId, defaultSorting, displayAllCards, displaySpecificDeck, allowPracticeAll, allowOnQueryChanged, quickToggle);
    }
}
