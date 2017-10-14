package stelztech.youknowehv4.database.quizcard;

import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.quiz.IQuizSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IQuizCardSchema {

    // COLUMNS NAME
    String TABLE_NAME = "quizcard";
    String COLUMN_NAME_QUIZ_ID = "quizid";
    String COLUMN_NAME_CARD_ID = "cardid";
    String COLUMN_NAME_NUM_FAILED = "numfail";
    String COLUMN_NAME_PASSED = "passed";
    String COLUMN_NAME_POSITION = "position";


    // ON CREATE
    String SQL_CREATE_TABLE_QUIZ_CARD = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COLUMN_NAME_CARD_ID + " INTEGER,"
            + COLUMN_NAME_QUIZ_ID + " INTEGER,"
            + COLUMN_NAME_NUM_FAILED + " INTEGER,"
            + COLUMN_NAME_POSITION + " INTEGER,"
            + COLUMN_NAME_PASSED + " BOOLEAN,"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_CARD_ID + ")"
            + " REFERENCES " + ICardSchema.TABLE_NAME + "(" + ICardSchema.COLUMN_NAME_CARD_ID + "),"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_QUIZ_ID + ")"
            + " REFERENCES " + IQuizSchema.TABLE_NAME + "(" + IQuizSchema.COLUMN_NAME_QUIZ_ID + ")"
            + " PRIMARY KEY (" + COLUMN_NAME_CARD_ID + ", " + COLUMN_NAME_QUIZ_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_QUIZ_CARD = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // COLUMNS
    String[] QUIZCARD_COLUMNS = new String[]{COLUMN_NAME_QUIZ_ID,
            COLUMN_NAME_CARD_ID, COLUMN_NAME_NUM_FAILED, COLUMN_NAME_PASSED, COLUMN_NAME_POSITION};
}
