package stelztech.youknowehv4.database.profile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.card.ICardDao;
import stelztech.youknowehv4.database.card.ICardSchema;

/**
 * Created by alex on 10/14/2017.
 */

public class ProfileDao extends DbContentProvider implements IProfileDao, IProfileSchema {

    public ProfileDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public List<Profile> fetchAllProfiles() {
        return null;
    }

    @Override
    public Profile fetchProfileFromId(int profileId) {
        return null;
    }

    @Override
    public boolean deleteProfile(int profileId) {
        return false;
    }

    @Override
    public int createProfile(String name) {
        return 0;
    }

    @Override
    public boolean updateProfile(int profileId, String name) {
        return false;
    }

    @Override
    public boolean updateProfileQuestionLabel(int profileId, String questionLabel) {
        return false;
    }

    @Override
    public boolean updateProfileAnswerLabel(int profileId, String answerLabel) {
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

    @Override
    protected Profile cursorToEntity(Cursor cursor) {
        return null;
    }

}
