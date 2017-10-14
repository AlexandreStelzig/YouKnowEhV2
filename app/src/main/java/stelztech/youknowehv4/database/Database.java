package stelztech.youknowehv4.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.carddeck.ICardDeckSchema;
import stelztech.youknowehv4.database.deck.IDeckSchema;
import stelztech.youknowehv4.database.profile.IProfileSchema;
import stelztech.youknowehv4.database.quiz.IQuizSchema;
import stelztech.youknowehv4.database.quizcard.IQuizCardSchema;
import stelztech.youknowehv4.database.user.IUserSchema;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Database extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "YouKnowEh.db";




    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ICardSchema.SQL_CREATE_TABLE_CARD);
        db.execSQL(IDeckSchema.SQL_CREATE_TABLE_DECK);
        db.execSQL(ICardDeckSchema.SQL_CREATE_TABLE_CARD_DECK);
        db.execSQL(IProfileSchema.SQL_CREATE_TABLE_PROFILE);
        db.execSQL(IUserSchema.SQL_CREATE_TABLE_USER);
        db.execSQL(IQuizSchema.SQL_CREATE_TABLE_QUIZ);
        db.execSQL(IQuizCardSchema.SQL_CREATE_TABLE_QUIZ_CARD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(ICardSchema.SQL_DELETE_TABLE_CARD);
        db.execSQL(IDeckSchema.SQL_DELETE_TABLE_DECK);
        db.execSQL(ICardDeckSchema.SQL_DELETE_TABLE_CARD_DECK);
        db.execSQL(IProfileSchema.SQL_DELETE_TABLE_PROFILE);
        db.execSQL(IUserSchema.SQL_DELETE_TABLE_USER);
        db.execSQL(IQuizSchema.SQL_DELETE_TABLE_QUIZ);
        db.execSQL(IQuizCardSchema.SQL_DELETE_TABLE_QUIZ_CARD);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
