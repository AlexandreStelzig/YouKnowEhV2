package stelztech.youknowehv4.database.quiz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import stelztech.youknowehv4.database.DbContentProvider;

/**
 * Created by alex on 10/14/2017.
 */

public class QuizDao extends DbContentProvider implements IQuizDao, IQuizSchema {

    public QuizDao(SQLiteDatabase db) {
        super(db);
    }

    @Override
    protected Quiz cursorToEntity(Cursor cursor) {
        int quizId = cursor.getInt(cursor.getColumnIndex(COLUMN_QUIZ_ID));
        String dateCreated = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));
        String dateFinished = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_FINISHED));
        boolean repeat = cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT)) > 0;
        boolean reverse = cursor.getInt(cursor.getColumnIndex(COLUMN_REVERSE)) > 0;
        String mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
        int profileId = cursor.getInt(cursor.getColumnIndex(COLUMN_PROFILE_ID));
        boolean reviewOnly = cursor.getInt(cursor.getColumnIndex(COLUMN_REVIEW_ONLY)) > 0;


        return new Quiz(quizId, dateCreated, dateFinished, Quiz.MODE.valueOf(mode), reverse,  repeat, reviewOnly, profileId);
    }
}
