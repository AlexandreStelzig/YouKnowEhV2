package stelztech.youknowehv4.database.profile;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
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
    public List<Profile> fetchAllProfiles() {
        return null;
    }

    @Override
    public Profile fetchProfileById(long profileId) {
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

    @Override
    protected Profile cursorToEntity(Cursor cursor) {

        long profileId = cursor.getLong(cursor.getColumnIndex(COLUMN_PROFILE_ID));
        String profileName = cursor.getString(cursor.getColumnIndex(COLUMN_PROFILE_NAME));
        Date dateAdded = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_CREATED)));
        String questionLabel = cursor.getString(cursor.getColumnIndex(COLUMN_QUESTION_LABEL));
        String answerLabel = cursor.getString(cursor.getColumnIndex(COLUMN_ANSWER_LABEL));
        long activeQuizId = cursor.getLong(cursor.getColumnIndex(COLUMN_ACTIVE_QUIZ_ID));


        return new Profile(profileId, profileName, dateAdded, questionLabel, answerLabel, activeQuizId);
    }

}
