package stelztech.youknowehv4.database.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.profile.IProfileDao;
import stelztech.youknowehv4.database.profile.IProfileSchema;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.helper.DateHelper;
import stelztech.youknowehv4.manager.SortingStateManager;

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

        cursor = super.query(USER_TABLE, USER_COLUMNS, null, null, COLUMN_USER_ID);
        User user = null;

        if (cursor.moveToFirst()) {
            user = (cursorToEntity(cursor));
            cursor.moveToNext();
        }

        cursor.close();
        return user;
    }

    @Override
    public long createUser() {

        if (fetchUser() == null) {
            ContentValues values = new ContentValues();

            String date = DateHelper.getDateNow();

            values.put(COLUMN_DATE_CREATED, date);
            values.put(COLUMN_ACTIVE_PROFILE_ID, -1);
            values.put(COLUMN_DEFAULT_SORTING, 0);
            values.put(COLUMN_ALLOW_PROFILE_DELETION, 0);
            values.put(COLUMN_DISPLAY_ALL_CARDS, 1);
            values.put(COLUMN_DISPLAY_SPECIFIC_DECK, 0);
            values.put(COLUMN_ALLOW_PRACTICE_ALL, 0);
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
    public boolean setActiveProfile(long profileId) {
        final String selectionArgs[] = {String.valueOf(fetchUser().getUserId())};
        final String selection = COLUMN_USER_ID + " = ?";

        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_ACTIVE_PROFILE_ID, profileId);

            return super.update(USER_TABLE, values, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleAllowProfileDeletion() {
        User user = fetchUser();
        final String selectionArgs[] = {String.valueOf(user.getUserId())};
        final String selection = COLUMN_USER_ID + " = ?";

        ContentValues values = new ContentValues();

        boolean currentIsProfileDeletion = user.isAllowProfileDeletion();
        boolean newIsProfileDeletion = !currentIsProfileDeletion;

        try {
            values.put(COLUMN_ALLOW_PROFILE_DELETION, newIsProfileDeletion);

            return super.update(USER_TABLE, values, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleAllowSearchOnQueryChanged() {
        User user = fetchUser();
        final String selectionArgs[] = {String.valueOf(user.getUserId())};
        final String selection = COLUMN_USER_ID + " = ?";

        ContentValues values = new ContentValues();

        boolean currentAllowOnQueryChange = user.isAllowOnQueryChanged();
        boolean nextAllowOnQueryChange = !currentAllowOnQueryChange;

        try {
            values.put(COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED, nextAllowOnQueryChange);

            return super.update(USER_TABLE, values, selection, selectionArgs) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public Profile fetchActiveProfile() {
        return null;
    }

    @Override
    protected User cursorToEntity(Cursor cursor) {
        long userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID));
        long activeProfileId = cursor.getLong(cursor.getColumnIndex(COLUMN_ACTIVE_PROFILE_ID));
        Date dateCreated = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_CREATED)));
        int defaultSorting = cursor.getInt(cursor.getColumnIndex(COLUMN_DEFAULT_SORTING));
        boolean allowProfileDeletion = cursor.getInt(cursor.getColumnIndex(COLUMN_ALLOW_PROFILE_DELETION)) > 0;
        boolean displayAllCards = cursor.getInt(cursor.getColumnIndex(COLUMN_DISPLAY_ALL_CARDS)) > 0;
        boolean displaySpecificDeck = cursor.getInt(cursor.getColumnIndex(COLUMN_DISPLAY_SPECIFIC_DECK)) > 0;
        boolean allowPracticeAll = cursor.getInt(cursor.getColumnIndex(COLUMN_ALLOW_PRACTICE_ALL)) > 0;
        boolean allowOnQueryChanged = cursor.getInt(cursor.getColumnIndex(COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED)) > 0;
        int quickToggle = cursor.getInt(cursor.getColumnIndex(COLUMN_QUICK_TOGGLE_REVIEW));


        return new User(userId, dateCreated, activeProfileId, defaultSorting, allowProfileDeletion, displayAllCards, displaySpecificDeck, allowPracticeAll, allowOnQueryChanged, quickToggle);
    }
}
