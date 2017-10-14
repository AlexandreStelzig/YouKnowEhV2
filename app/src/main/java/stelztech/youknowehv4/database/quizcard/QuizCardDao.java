package stelztech.youknowehv4.database.quizcard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.quiz.IQuizDao;
import stelztech.youknowehv4.database.quiz.IQuizSchema;

/**
 * Created by alex on 10/14/2017.
 */

public class QuizCardDao extends DbContentProvider implements IQuizCardDao, IQuizCardSchema {

    public QuizCardDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected QuizCard cursorToEntity(Cursor cursor) {
        return null;
    }
}
