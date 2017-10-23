package stelztech.youknowehv4.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import stelztech.youknowehv4.database.card.CardDao;
import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.carddeck.CardDeckDao;
import stelztech.youknowehv4.database.carddeck.ICardDeckSchema;
import stelztech.youknowehv4.database.deck.DeckDao;
import stelztech.youknowehv4.database.deck.IDeckSchema;
import stelztech.youknowehv4.database.profile.IProfileSchema;
import stelztech.youknowehv4.database.profile.ProfileDao;
import stelztech.youknowehv4.database.quiz.IQuizSchema;
import stelztech.youknowehv4.database.quiz.QuizDao;
import stelztech.youknowehv4.database.quizcard.IQuizCardSchema;
import stelztech.youknowehv4.database.quizcard.QuizCardDao;
import stelztech.youknowehv4.database.user.IUserSchema;
import stelztech.youknowehv4.database.user.UserDao;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Database {
    private static final String TAG = "YouKnowEhDatabase";
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "YouKnowEh.db";
    private static Database database = null;
    private static DatabaseHelper mDatabaseHelper;

    // tables
    public CardDao mCardDao;
    public CardDeckDao mCardDeckDao;
    public DeckDao mDeckDao;
    public ProfileDao mProfileDao;
    public QuizDao mQuizDao;
    public QuizCardDao mQuizCardDao;
    public UserDao mUserDao;

    public static Database getInstance() {
        if (database == null) {
            Log.e(TAG, "Cannot get instance of database - database is not open");
            return null;
        }
        return database;
    }

    private Database(Context context) {
        mDatabaseHelper = new DatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase();

        mCardDao = new CardDao(sqLiteDatabase);
        mCardDeckDao = new CardDeckDao(sqLiteDatabase);
        mDeckDao = new DeckDao(sqLiteDatabase);
        mProfileDao = new ProfileDao(sqLiteDatabase);
        mQuizDao = new QuizDao(sqLiteDatabase);
        mQuizCardDao = new QuizCardDao(sqLiteDatabase);
        mUserDao = new UserDao(sqLiteDatabase);
    }

    public static void open(Context context) {
        if (database == null) {
            database = new Database(context);
        } else {
            Log.e(TAG, "Cannot open database - database is already opened");
        }
    }

    public void close() {
        if (database != null) {
            mDatabaseHelper.close();
            database = null;
        }else {
            Log.e(TAG, "Cannot close database - database is not opened");
        }
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
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

}
