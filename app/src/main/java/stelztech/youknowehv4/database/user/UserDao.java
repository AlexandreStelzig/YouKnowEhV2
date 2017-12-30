package stelztech.youknowehv4.database.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.utilities.DateUtilities;

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

            String date = DateUtilities.getDateNowString();


            // TODO move default values to config file
            values.put(COLUMN_DATE_CREATED, date);
            values.put(COLUMN_ACTIVE_PROFILE_ID, -1);

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
    public Profile fetchActiveProfile() {
        return Database.mProfileDao.fetchProfileById(fetchUser().getActiveProfileId());
    }


    @Override
    protected User cursorToEntity(Cursor cursor) {
        int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
        int activeProfileId = cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE_PROFILE_ID));
        String dateCreated = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));


        return new User(userId, dateCreated, activeProfileId);
    }
}
