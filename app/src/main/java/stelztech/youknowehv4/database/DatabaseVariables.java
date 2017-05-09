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
            + TableCard.COLUMN_NAME_DATE_MODIFIED + " DATE,"
            + TableCard.COLUMN_NAME_ARCHIVED + " BOOLEAN,"
            + TableCard.COLUMN_NAME_PROFILE_ID + " INTEGER,"
            + " FOREIGN KEY " + "(" + TableCard.COLUMN_NAME_PROFILE_ID + ")"
            + " REFERENCES " + TableProfile.TABLE_NAME + "(" + TableProfile.COLUMN_NAME_PROFILE_ID + ")" + " );";

    public static final String SQL_CREATE_TABLE_DECK = "CREATE TABLE "
            + TableDeck.TABLE_NAME + " ("
            + TableDeck.COLUMN_NAME_DECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableDeck.COLUMN_NAME_DECK_NAME + " TEXT NOT NULL,"
            + TableDeck.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableDeck.COLUMN_NAME_DATE_MODIFIED + " DATE,"
            + TableDeck.COLUMN_NAME_PROFILE_ID + " INTEGER,"
            + TableDeck.COLUMN_NAME_POSITION + " INTEGER,"
            + " FOREIGN KEY " + "(" + TableDeck.COLUMN_NAME_PROFILE_ID + ")"
            + " REFERENCES " + TableProfile.TABLE_NAME + "(" + TableProfile.COLUMN_NAME_PROFILE_ID + ")" + " );";

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

    public static final String SQL_CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TableProfile.TABLE_NAME + " ("
            + TableProfile.COLUMN_NAME_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableProfile.COLUMN_NAME_PROFILE_NAME + " TEXT NOT NULL,"
            + TableProfile.COLUMN_NAME_QUESTION_LABEL + " TEXT NOT NULL,"
            + TableProfile.COLUMN_NAME_ANSWER_LABEL + " TEXT NOT NULL,"
            + TableProfile.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableProfile.COLUMN_NAME_DATE_MODIFIED + " DATE,"
            + TableProfile.COLUMN_NAME_ACTIVE + " BOOLEAN" + " );";


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
        public static final String COLUMN_NAME_PROFILE_ID = "profileid";
        public static final String COLUMN_NAME_ARCHIVED = "archived";
    }

    public static abstract class TableDeck implements BaseColumns {
        public static final String TABLE_NAME = "package";
        public static final String COLUMN_NAME_DECK_ID = "deckid";
        public static final String COLUMN_NAME_DECK_NAME = "packagename";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DATE_MODIFIED = "datemodified";
        public static final String COLUMN_NAME_PROFILE_ID = "profileid";
        public static final String COLUMN_NAME_POSITION = "position";
    }


    public static abstract class TableCardDeck implements BaseColumns{
        public static final String TABLE_NAME = "carddeck";
        public static final String COLUMN_NAME_DECK_ID = "deckid";
        public static final String COLUMN_NAME_CARD_ID = "cardid";
        public static final String COLUMN_NAME_DATE_ADDED = "dateadded";
        public static final String COLUMN_NAME_IS_PRACTICE = "ispractice";

    }

    public static abstract class TableProfile implements BaseColumns{
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_PROFILE_ID = "profileid";
        public static final String COLUMN_NAME_PROFILE_NAME = "profilename";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DATE_MODIFIED = "datemodified";
        public static final String COLUMN_NAME_QUESTION_LABEL = "questionlabel";
        public static final String COLUMN_NAME_ANSWER_LABEL = "answerlabel";
        public static final String COLUMN_NAME_ACTIVE = "active";
    }
}
