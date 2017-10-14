package stelztech.youknowehv4.database.quiz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.profile.IProfileDao;
import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public class QuizDao extends DbContentProvider implements IQuizDao, IQuizSchema {

    public QuizDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected Quiz cursorToEntity(Cursor cursor) {
        return null;
    }
}
