package stelztech.youknowehv4.database.user;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.profile.IProfileDao;
import stelztech.youknowehv4.database.profile.IProfileSchema;
import stelztech.youknowehv4.database.profile.Profile;

/**
 * Created by alex on 10/14/2017.
 */

public class UserDao extends DbContentProvider implements IUserDao, IUserSchema {


    public UserDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected User cursorToEntity(Cursor cursor) {
        return null;
    }

    @Override
    public User fetchUser() {
        return null;
    }

    @Override
    public long createUser() {
        return 0;
    }

    @Override
    public boolean updateDefaultSortPosition(int position) {
        return false;
    }

    @Override
    public boolean updateQuickToggleReviewHours(int hours) {
        return false;
    }

    @Override
    public boolean setActiveProfile(long profileId) {
        return false;
    }

    @Override
    public boolean toggleAllowProfileDeletion() {
        return false;
    }

    @Override
    public boolean toggleAllowSearchOnQueryChanged() {
        return false;
    }

    @Override
    public Profile fetchActiveProfile() {
        return null;
    }
}
