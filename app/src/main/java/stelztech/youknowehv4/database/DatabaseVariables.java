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
            + TableCardDeck.COLUMN_NAME_DATE_TOGGLE_PRACTICE + " DATE,"
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
            + TableProfile.COLUMN_NAME_ACTIVE_QUIZ_ID + " INTEGER,"
            + TableProfile.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableProfile.COLUMN_NAME_DATE_MODIFIED + " DATE,"
            + " FOREIGN KEY " + "(" + TableProfile.COLUMN_NAME_ACTIVE_QUIZ_ID + ")"
            + " REFERENCES " + TableQuiz.TABLE_NAME + "(" + TableQuiz.COLUMN_NAME_QUIZ_ID + ")" + " );";

    public static final String SQL_CREATE_TABLE_USER = "CREATE TABLE "
            + TableUser.TABLE_NAME + " ("
            + TableUser.COLUMN_NAME_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableUser.COLUMN_NAME_DEFAULT_SORTING + " INTEGER NOT NULL,"
            + TableUser.COLUMN_NAME_ALLOW_PROFILE_DELETION + " BOOLEAN,"
            + TableUser.COLUMN_NAME_DISPLAY_ALL_CARDS + " BOOLEAN,"
            + TableUser.COLUMN_NAME_DISPLAY_SPECIFIC_DECK + " BOOLEAN,"
            + TableUser.COLUMN_NAME_QUICK_TOGGLE_REVIEW + " INTEGER,"
            + TableUser.COLUMN_NAME_ALLOW_SEARCH_ON_QUERY_CHANGED + " BOOLEAN,"
            + TableUser.COLUMN_NAME_ALLOW_PRACTICE_ALL + " BOOLEAN,"
            + TableUser.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableUser.COLUMN_NAME_ACTIVE_PROFILE_ID + " TEXT NOT NULL,"
            + " FOREIGN KEY " + "(" + TableUser.COLUMN_NAME_ACTIVE_PROFILE_ID + ")"
            + " REFERENCES " + TableProfile.TABLE_NAME + "(" + TableProfile.COLUMN_NAME_PROFILE_ID + ") " + ");";

    public static final String SQL_CREATE_TABLE_QUIZ = "CREATE TABLE "
            + TableQuiz.TABLE_NAME + " ("
            + TableQuiz.COLUMN_NAME_QUIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + TableQuiz.COLUMN_NAME_DATE_CREATED + " DATE,"
            + TableQuiz.COLUMN_NAME_DATE_FINISHED + " DATE,"
            + TableQuiz.COLUMN_NAME_MODE + " TEXT,"
            + TableQuiz.COLUMN_NAME_REPEAT + " BOOLEAN,"
            + TableQuiz.COLUMN_NAME_PROFILE_ID + " INTEGER,"
            + TableQuiz.COLUMN_NAME_REVIEW_ONLY + " BOOLEAN,"
            + TableQuiz.COLUMN_NAME_REVERSE + " BOOLEAN,"
            + " FOREIGN KEY " + "(" + TableQuiz.COLUMN_NAME_PROFILE_ID + ")"
            + " REFERENCES " + TableProfile.TABLE_NAME + "(" + TableProfile.COLUMN_NAME_PROFILE_ID + ")" + " );";

    public static final String SQL_CREATE_TABLE_QUIZ_CARD = "CREATE TABLE "
            + TableQuizCard.TABLE_NAME + " ("
            + TableQuizCard.COLUMN_NAME_CARD_ID + " INTEGER,"
            + TableQuizCard.COLUMN_NAME_QUIZ_ID + " INTEGER,"
            + TableQuizCard.COLUMN_NAME_NUM_FAILED + " INTEGER,"
            + TableQuizCard.COLUMN_NAME_POSITION + " INTEGER,"
            + TableQuizCard.COLUMN_NAME_PASSED + " BOOLEAN,"
            + " FOREIGN KEY " + "(" + TableQuizCard.COLUMN_NAME_CARD_ID + ")"
            + " REFERENCES " + TableCard.TABLE_NAME + "(" + TableCard.COLUMN_NAME_CARD_ID + "),"
            + " FOREIGN KEY " + "(" + TableQuizCard.COLUMN_NAME_QUIZ_ID + ")"
            + " REFERENCES " + TableQuiz.TABLE_NAME + "(" + TableQuiz.COLUMN_NAME_QUIZ_ID + ")"
            + " PRIMARY KEY (" + TableQuizCard.COLUMN_NAME_CARD_ID + ", " + TableQuizCard.COLUMN_NAME_QUIZ_ID + ") " + ");";


    public static final String SQL_DELETE_TABLE_CARD = "DROP TABLE IF EXISTS " + TableCard.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_DECK = "DROP TABLE IF EXISTS " + TableDeck.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_CARD_DECK = "DROP TABLE IF EXISTS " + TableCardDeck.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_USER = "DROP TABLE IF EXISTS " + TableUser.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_PROFILE = "DROP TABLE IF EXISTS " + TableProfile.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_QUIZ = "DROP TABLE IF EXISTS " + TableQuiz.TABLE_NAME;
    public static final String SQL_DELETE_TABLE_QUIZ_CARD = "DROP TABLE IF EXISTS " + TableQuizCard.TABLE_NAME;

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
        public static final String COLUMN_NAME_DATE_TOGGLE_PRACTICE = "datetogglepractice";

    }

    public static abstract class TableProfile implements BaseColumns{
        public static final String TABLE_NAME = "profile";
        public static final String COLUMN_NAME_PROFILE_ID = "profileid";
        public static final String COLUMN_NAME_PROFILE_NAME = "profilename";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DATE_MODIFIED = "datemodified";
        public static final String COLUMN_NAME_QUESTION_LABEL = "questionlabel";
        public static final String COLUMN_NAME_ANSWER_LABEL = "answerlabel";
        public static final String COLUMN_NAME_ACTIVE_QUIZ_ID = "activequizid";
    }

    public static abstract class TableUser implements BaseColumns{
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_USER_ID = "userid";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DEFAULT_SORTING = "defaultsorting";
        public static final String COLUMN_NAME_ACTIVE_PROFILE_ID = "activeprofile";
        public static final String COLUMN_NAME_ALLOW_PROFILE_DELETION = "allowprofiledeletion";
        public static final String COLUMN_NAME_DISPLAY_ALL_CARDS = "displayallcards";
        public static final String COLUMN_NAME_DISPLAY_SPECIFIC_DECK = "displayspecificdeck";
        public static final String COLUMN_NAME_ALLOW_PRACTICE_ALL = "allpracticeall";
        public static final String COLUMN_NAME_ALLOW_SEARCH_ON_QUERY_CHANGED = "allowsearchonquerychange";
        public static final String COLUMN_NAME_QUICK_TOGGLE_REVIEW = "quicktogglereview";
    }

    public static abstract class TableQuiz implements BaseColumns{
        public static final String TABLE_NAME = "quiz";
        public static final String COLUMN_NAME_QUIZ_ID = "quizid";
        public static final String COLUMN_NAME_DATE_CREATED = "datecreated";
        public static final String COLUMN_NAME_DATE_FINISHED = "datefinish";
        public static final String COLUMN_NAME_REPEAT = "repeat";
        public static final String COLUMN_NAME_REVERSE = "reverse";
        public static final String COLUMN_NAME_MODE = "mode";
        public static final String COLUMN_NAME_PROFILE_ID = "profileid";
        public static final String COLUMN_NAME_REVIEW_ONLY = "reviewonly";
    }

    public static abstract class TableQuizCard implements BaseColumns{
        public static final String TABLE_NAME = "quizcard";
        public static final String COLUMN_NAME_QUIZ_ID = "quizid";
        public static final String COLUMN_NAME_CARD_ID = "cardid";
        public static final String COLUMN_NAME_NUM_FAILED = "numfail";
        public static final String COLUMN_NAME_PASSED = "passed";
        public static final String COLUMN_NAME_POSITION = "position";
    }
}
