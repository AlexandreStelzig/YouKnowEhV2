package stelztech.youknowehv4.database;

import android.provider.BaseColumns;

/**
 * Created by Alexandre on 4/20/2016.
 */
public final class DatabaseVariables {


    public static final String SQL_CREATE_TABLE_WORD = "CREATE TABLE "
            + TableWord.TABLE_NAME + " ("
            + TableWord.COLUMN_NAME_ID_WORD + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableWord.COLUMN_NAME_QUESTION + " TEXT NOT NULL,"
            + TableWord.COLUMN_NAME_ANSWER + " TEXT NOT NULL,"
            + TableWord.COLUMN_NAME_MORE_INFORMATION + " TEXT,"
            + TableWord.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableWord.COLUMN_NAME_DATE_MODIFIED + " DATE" + " );";

    public static final String SQL_CREATE_TABLE_DECK = "CREATE TABLE "
            + TableDeck.TABLE_NAME + " ("
            + TableDeck.COLUMN_NAME_ID_DECK + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableDeck.COLUMN_NAME_DECK_NAME + " TEXT NOT NULL,"
            + TableDeck.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableDeck.COLUMN_NAME_DATE_MODIFIED + " DATE" + " );";

    public static final String SQL_CREATE_TABLE_WORD_DECK = "CREATE TABLE "
            + TableWordDeck.TABLE_NAME + " ("
            + TableWordDeck.COLUMN_NAME_ID_WORD + " INTEGER,"
            + TableWordDeck.COLUMN_NAME_ID_DECK + " INTEGER,"
            + TableWordDeck.COLUMN_NAME_IS_PRACTICE + " BOOLEAN,"
            + TableWordDeck.COLUMN_NAME_DATE_ADDED + "DATE,"
            + " FOREIGN KEY " + "(" + TableWordDeck.COLUMN_NAME_ID_WORD + ")"
            + " REFERENCES " + TableWord.TABLE_NAME + "(" + TableWord.COLUMN_NAME_ID_WORD + "),"
            + " FOREIGN KEY " + "(" + TableWordDeck.COLUMN_NAME_ID_DECK + ")"
            + " REFERENCES " + TableDeck.TABLE_NAME + "(" + TableDeck.COLUMN_NAME_ID_DECK + ")"
            + " PRIMARY KEY (" + TableWordDeck.COLUMN_NAME_ID_WORD + ", " + TableWordDeck.COLUMN_NAME_ID_DECK + ") " + ");";



    public static final String SQL_DELETE_TABLE_WORD = "DROP TABLE IF EXISTS " + TableWord.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_DECK = "DROP TABLE IF EXISTS " + TableDeck.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_WORD_DECK = "DROP TABLE IF EXISTS " + TableWordDeck.TABLE_NAME;

    public static abstract class TableWord implements BaseColumns {
        public static final String TABLE_NAME = "word";
        public static final String COLUMN_NAME_ID_WORD = "idword";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_ANSWER = "answer";
        public static final String COLUMN_NAME_MORE_INFORMATION = "moreinfo";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DATE_MODIFIED = "datemodified";
    }

    public static abstract class TableDeck implements BaseColumns {
        public static final String TABLE_NAME = "package";
        public static final String COLUMN_NAME_ID_DECK = "iddeck";
        public static final String COLUMN_NAME_DECK_NAME = "packagename";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DATE_MODIFIED = "datemodified";
    }


    public static abstract class TableWordDeck implements BaseColumns{
        public static final String TABLE_NAME = "wordpackage";
        public static final String COLUMN_NAME_ID_DECK = "iddeck";
        public static final String COLUMN_NAME_ID_WORD = "idword";
        public static final String COLUMN_NAME_DATE_ADDED = "dateadded";
        public static final String COLUMN_NAME_IS_PRACTICE = "ispractice";
    }

}
