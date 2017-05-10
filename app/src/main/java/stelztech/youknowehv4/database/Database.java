package stelztech.youknowehv4.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class Database extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "YouKnowEh.db";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // relation many to many from Deck -> Work


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseVariables.SQL_CREATE_TABLE_CARD);
        db.execSQL(DatabaseVariables.SQL_CREATE_TABLE_DECK);
        db.execSQL(DatabaseVariables.SQL_CREATE_TABLE_CARD_DECK);
        db.execSQL(DatabaseVariables.SQL_CREATE_TABLE_PROFILE);
        db.execSQL(DatabaseVariables.SQL_CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseVariables.SQL_DELETE_TABLE_DECK);
        db.execSQL(DatabaseVariables.SQL_DELETE_TABLE_CARD);
        db.execSQL(DatabaseVariables.SQL_DELETE_TABLE_CARD_DECK);
        db.execSQL(DatabaseVariables.SQL_DELETE_TABLE_PROFILE);
        db.execSQL(DatabaseVariables.SQL_DELETE_TABLE_USER);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
