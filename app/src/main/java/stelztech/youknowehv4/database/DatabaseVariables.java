package stelztech.youknowehv4.database;

import android.provider.BaseColumns;

/**
 * Created by Alexandre on 4/20/2016.
 */
public final class DatabaseVariables {


    public static final String SQL_CREATE_TABLE_CARD = "CREATE TABLE "
            + TableCard.TABLE_NAME + " ("
            + TableCard.COLUMN_NAME_CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableCard.COLUMN_NAME_QUESTION + " TEXT NOT NULL,"
            + TableCard.COLUMN_NAME_ANSWER + " TEXT NOT NULL,"
            + TableCard.COLUMN_NAME_MORE_INFORMATION + " TEXT,"
            + TableCard.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableCard.COLUMN_NAME_DATE_MODIFIED + " DATE" + " );";

    public static final String SQL_CREATE_TABLE_DECK = "CREATE TABLE "
            + TableDeck.TABLE_NAME + " ("
            + TableDeck.COLUMN_NAME_DECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableDeck.COLUMN_NAME_DECK_NAME + " TEXT NOT NULL,"
            + TableDeck.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableDeck.COLUMN_NAME_DATE_MODIFIED + " DATE" + " );";

    public static final String SQL_CREATE_TABLE_CARD_DECK = "CREATE TABLE "
            + TableCardDeck.TABLE_NAME + " ("
            + TableCardDeck.COLUMN_NAME_CARD_ID + " INTEGER,"
            + TableCardDeck.COLUMN_NAME_DECK_ID + " INTEGER,"
            + TableCardDeck.COLUMN_NAME_IS_PRACTICE + " BOOLEAN,"
            + TableCardDeck.COLUMN_NAME_DATE_ADDED + " DATE,"
            + " FOREIGN KEY " + "(" + TableCardDeck.COLUMN_NAME_CARD_ID + ")"
            + " REFERENCES " + TableCard.TABLE_NAME + "(" + TableCard.COLUMN_NAME_CARD_ID + "),"
            + " FOREIGN KEY " + "(" + TableCardDeck.COLUMN_NAME_DECK_ID + ")"
            + " REFERENCES " + TableDeck.TABLE_NAME + "(" + TableDeck.COLUMN_NAME_DECK_ID + ")"
            + " PRIMARY KEY (" + TableCardDeck.COLUMN_NAME_CARD_ID + ", " + TableCardDeck.COLUMN_NAME_DECK_ID + ") " + ");";



    public static final String SQL_DELETE_TABLE_CARD = "DROP TABLE IF EXISTS " + TableCard.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_DECK = "DROP TABLE IF EXISTS " + TableDeck.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_CARD_DECK = "DROP TABLE IF EXISTS " + TableCardDeck.TABLE_NAME;

    public static abstract class TableCard implements BaseColumns {
        public static final String TABLE_NAME = "card";
        public static final String COLUMN_NAME_CARD_ID = "cardid";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_ANSWER = "answer";
        public static final String COLUMN_NAME_MORE_INFORMATION = "moreinfo";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DATE_MODIFIED = "datemodified";
    }

    public static abstract class TableDeck implements BaseColumns {
        public static final String TABLE_NAME = "package";
        public static final String COLUMN_NAME_DECK_ID = "deckid";
        public static final String COLUMN_NAME_DECK_NAME = "packagename";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DATE_MODIFIED = "datemodified";
    }


    public static abstract class TableCardDeck implements BaseColumns{
        public static final String TABLE_NAME = "carddeck";
        public static final String COLUMN_NAME_DECK_ID = "deckid";
        public static final String COLUMN_NAME_CARD_ID = "wordid";
        public static final String COLUMN_NAME_DATE_ADDED = "dateadded";
        public static final String COLUMN_NAME_IS_PRACTICE = "ispractice";
    }

}
