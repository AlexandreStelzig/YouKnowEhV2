package stelztech.youknowehv4.database.quiz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
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
        long quizId = cursor.getLong(cursor.getColumnIndex(COLUMN_QUIZ_ID));
        Date dateCreated = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_CREATED)));
        Date dateFinished = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_FINISHED)));
        boolean repeat = cursor.getInt(cursor.getColumnIndex(COLUMN_REPEAT)) > 0;
        boolean reverse = cursor.getInt(cursor.getColumnIndex(COLUMN_REVERSE)) > 0;
        String mode = cursor.getString(cursor.getColumnIndex(COLUMN_MODE));
        long profileId = cursor.getLong(cursor.getColumnIndex(COLUMN_PROFILE_ID));
        boolean reviewOnly = cursor.getInt(cursor.getColumnIndex(COLUMN_REVIEW_ONLY)) > 0;


        return new Quiz(quizId, dateCreated, dateFinished, Quiz.MODE.valueOf(mode), reverse,  repeat, reviewOnly, profileId);
    }
}
