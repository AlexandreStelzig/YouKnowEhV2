package stelztech.youknowehv4.database.quizcard;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.quiz.IQuizDao;
import stelztech.youknowehv4.database.quiz.IQuizSchema;
import stelztech.youknowehv4.database.quiz.Quiz;

/**
 * Created by alex on 10/14/2017.
 */

public class QuizCardDao extends DbContentProvider implements IQuizCardDao, IQuizCardSchema {

    public QuizCardDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected QuizCard cursorToEntity(Cursor cursor) {

        long quizId = cursor.getLong(cursor.getColumnIndex(COLUMN_QUIZ_ID));
        long cardId = cursor.getLong(cursor.getColumnIndex(COLUMN_CARD_ID));
        int numFailed = cursor.getInt(cursor.getColumnIndex(COLUMN_NUM_FAILED));
        boolean passed = cursor.getInt(cursor.getColumnIndex(COLUMN_PASSED)) > 0;
        int position = cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION));

        return new QuizCard(quizId, cardId, passed, numFailed, position);
    }
}
