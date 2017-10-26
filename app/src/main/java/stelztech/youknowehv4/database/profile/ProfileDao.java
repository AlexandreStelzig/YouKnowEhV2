package stelztech.youknowehv4.database.profile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import stelztech.youknowehv4.database.DbContentProvider;

/**
 * Created by alex on 10/14/2017.
 */

public class ProfileDao extends DbContentProvider implements IProfileDao, IProfileSchema {

    public ProfileDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected Profile cursorToEntity(Cursor cursor) {
        return null;
    }

    @Override
    public List<Profile> fetchAllProfiles() {
        return null;
    }

    @Override
    public Profile fetchProfileFromId(long profileId) {
        return null;
    }

    @Override
    public boolean deleteProfile(long profileId) {
        return false;
    }

    @Override
    public long createProfile(String name) {
        return 0;
    }

    @Override
    public boolean updateProfile(long profileId, String name) {
        return false;
    }

    @Override
    public boolean updateProfileQuestionLabel(long profileId, String questionLabel) {
        return false;
    }

    @Override
    public boolean updateProfileAnswerLabel(long profileId, String answerLabel) {
        return false;
    }

    @Override
    public boolean toggleAllowPracticeAll() {
        return false;
    }

    @Override
    public boolean toggleDisplayNumDecksAllCards() {
        return false;
    }

    @Override
    public boolean toggleDisplayNumDecksSpecificCard() {
        return false;
    }
}
